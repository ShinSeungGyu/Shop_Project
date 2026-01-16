package com.shop.controller;


import com.shop.dto.MemberFormDto;
import com.shop.dto.MemberProfileDto;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import com.shop.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/new")
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping("/new")
    public String newMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){
            return "member/memberForm";
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String loginMember(){
        return "/member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(@RequestParam(value="errorType", required=false, defaultValue="baseError") String errorType, Model model){
        if ("oauth2Error".equals(errorType)) {
            model.addAttribute("loginErrorMsg", "구글 로그인에 실패하였습니다.");
        } else {
            model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        }
        return "/member/memberLoginForm";
    }

    @GetMapping(value="/profile")
    public String memberProfile(Principal principal, Model model){
        Member member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + principal.getName()));
        MemberProfileDto memberProfileDto = new MemberProfileDto();
        memberProfileDto.setName(member.getName());
        memberProfileDto.setEmail(member.getEmail());
        memberProfileDto.setAddress(member.getAddress());
        model.addAttribute("member", member);
        model.addAttribute("memberProfileDto", memberProfileDto);
        return "/member/profile";
    }
    @PostMapping("/profile")
    public String memberProfile(@Valid @ModelAttribute MemberProfileDto memberProfileDto, BindingResult bindingResult, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        String currentUserName = principal.getName(); // 현재 로그인한 사용자의 이메일 (principal.getName()이 이메일이라고 가정)

        //유효성 검사 실패 시
        if (bindingResult.hasErrors()) {
            Optional<Member> memberOptional = memberRepository.findByEmail(currentUserName);
            if (memberOptional.isPresent()) {
                model.addAttribute("member", memberOptional.get());
            } else {
                model.addAttribute("errorMessage", "사용자 정보를 불러올 수 없습니다.");
            }
            return "/member/profile";
        }

        // ⭐ 2. 유효성 검사 통과 후 실제 정보 수정 처리
        try {
            memberService.updateMember(currentUserName, memberProfileDto);
            // 성공 시 Flash Attribute를 사용하여 리다이렉트 후에도 메시지를 한 번만 소비되도록 전달합니다.
            redirectAttributes.addFlashAttribute("successMessage", "개인정보가 성공적으로 수정되었습니다.");
            return "redirect:/members/profile"; // 프로필 페이지로 리다이렉트하여 성공 메시지 표시
        } catch (Exception e) {
            // ⭐ 3. 정보 수정 실패 시
            // 에러 메시지를 모델에 담아 폼 템플릿으로 다시 돌아갑니다.
            model.addAttribute("errorMessage", "개인정보 수정에 실패했습니다: " + e.getMessage());

            // 마찬가지로 HTML의 <p th:text="${member.name}"> 에 사용될 member 객체를 모델에 다시 추가합니다.
            Optional<Member> memberOptional = memberRepository.findByEmail(currentUserName);
            if (memberOptional.isPresent()) {
                model.addAttribute("member", memberOptional.get());
            } else {
                // 이 경우는 매우 드물겠지만, 로그인된 사용자의 정보가 DB에 없는 경우 처리
                model.addAttribute("errorMessage", "로그인 사용자 정보를 찾을 수 없습니다.");
            }

            // memberProfileDto는 @ModelAttribute 덕분에 이미 모델에 있습니다.
            return "/member/profile"; // ⭐ 프로필 수정 폼을 보여주는 템플릿 이름
        }
    }
}