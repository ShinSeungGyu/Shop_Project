package com.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AddressController {

    @RequestMapping("/jusoPopup")
    public String jusoPopup(
            @RequestParam(value = "inputYn", required = false, defaultValue = "N") String inputYn,
            // 주소 API에서 넘겨주는 파라미터들을 모두 @RequestParam으로 받아서 Model에 추가합니다.
            // required = false로 설정하여, 파라미터가 없을 경우 null이 들어오도록 하고,
            // Model에 추가할 때는 null 체크 후 빈 문자열로 대체하여 Thymeleaf에서 오류가 나지 않도록 합니다.
            @RequestParam(value = "roadFullAddr", required = false) String roadFullAddr,
            @RequestParam(value = "roadAddrPart1", required = false) String roadAddrPart1,
            @RequestParam(value = "roadAddrPart2", required = false) String roadAddrPart2,
            @RequestParam(value = "engAddr", required = false) String engAddr,
            @RequestParam(value = "jibunAddr", required = false) String jibunAddr,
            @RequestParam(value = "zipNo", required = false) String zipNo,
            @RequestParam(value = "addrDetail", required = false) String addrDetail,
            @RequestParam(value = "admCd", required = false) String admCd,
            @RequestParam(value = "rnMgtSn", required = false) String rnMgtSn,
            @RequestParam(value = "bdMgtSn", required = false) String bdMgtSn,
            @RequestParam(value = "detBdNmList", required = false) String detBdNmList,
            @RequestParam(value = "bdNm", required = false) String bdNm,
            @RequestParam(value = "bdKdcd", required = false) String bdKdcd,
            @RequestParam(value = "siNm", required = false) String siNm,
            @RequestParam(value = "sggNm", required = false) String sggNm,
            @RequestParam(value = "emdNm", required = false) String emdNm,
            @RequestParam(value = "liNm", required = false) String liNm,
            @RequestParam(value = "rn", required = false) String rn,
            @RequestParam(value = "udrtYn", required = false) String udrtYn,
            @RequestParam(value = "buldMnnm", required = false) String buldMnnm,
            @RequestParam(value = "buldSlno", required = false) String buldSlno,
            @RequestParam(value = "mtYn", required = false) String mtYn,
            @RequestParam(value = "lnbrMnnm", required = false) String lnbrMnnm,
            @RequestParam(value = "lnbrSlno", required = false) String lnbrSlno,
            @RequestParam(value = "emdNo", required = false) String emdNo,
            Model model) {

        // 모든 파라미터를 모델에 추가하여 Thymeleaf 템플릿에서 사용할 수 있도록 합니다.
        // null 방지 처리를 통해 Thymeleaf JavaScript 인라인에서 오류를 방지합니다.
        model.addAttribute("inputYn", inputYn);
        model.addAttribute("roadFullAddr", roadFullAddr != null ? roadFullAddr : "");
        model.addAttribute("roadAddrPart1", roadAddrPart1 != null ? roadAddrPart1 : "");
        model.addAttribute("roadAddrPart2", roadAddrPart2 != null ? roadAddrPart2 : "");
        model.addAttribute("engAddr", engAddr != null ? engAddr : "");
        model.addAttribute("jibunAddr", jibunAddr != null ? jibunAddr : "");
        model.addAttribute("zipNo", zipNo != null ? zipNo : "");
        model.addAttribute("addrDetail", addrDetail != null ? addrDetail : "");
        model.addAttribute("admCd", admCd != null ? admCd : "");
        model.addAttribute("rnMgtSn", rnMgtSn != null ? rnMgtSn : "");
        model.addAttribute("bdMgtSn", bdMgtSn != null ? bdMgtSn : "");
        model.addAttribute("detBdNmList", detBdNmList != null ? detBdNmList : "");
        model.addAttribute("bdNm", bdNm != null ? bdNm : "");
        model.addAttribute("bdKdcd", bdKdcd != null ? bdKdcd : "");
        model.addAttribute("siNm", siNm != null ? siNm : "");
        model.addAttribute("sggNm", sggNm != null ? sggNm : "");
        model.addAttribute("emdNm", emdNm != null ? emdNm : "");
        model.addAttribute("liNm", liNm != null ? liNm : "");
        model.addAttribute("rn", rn != null ? rn : "");
        model.addAttribute("udrtYn", udrtYn != null ? udrtYn : "");
        model.addAttribute("buldMnnm", buldMnnm != null ? buldMnnm : "");
        model.addAttribute("buldSlno", buldSlno != null ? buldSlno : "");
        model.addAttribute("mtYn", mtYn != null ? mtYn : "");
        model.addAttribute("lnbrMnnm", lnbrMnnm != null ? lnbrMnnm : "");
        model.addAttribute("lnbrSlno", lnbrSlno != null ? lnbrSlno : "");
        model.addAttribute("emdNo", emdNo != null ? emdNo : "");

        // 이 컨트롤러는 templates/jusoPopup.html 파일을 반환합니다.
        return "jusoPopup";
    }
}
