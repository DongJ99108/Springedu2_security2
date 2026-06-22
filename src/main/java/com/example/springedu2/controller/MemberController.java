package com.example.springedu2.controller;

import com.example.springedu2.Service.MemberService;
import com.example.springedu2.dto.MemberCreateForm;
import com.example.springedu2.dto.MemberUpdateForm;
import com.example.springedu2.entity.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    /* 생성자 주입 : @RequiredArgsConstructor 가 해결해줌
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    */

    // 회원가입 페이지로 이동
    @GetMapping("/members/register")
    public String registerForm(Model model) {
        model.addAttribute("memberForm", new MemberCreateForm());
        return "memberRegister"; // membersRegister.html
    }

    // 회원가입
    @PostMapping("/members/register")
    public String registerMember(
            @Valid @ModelAttribute("memberForm") MemberCreateForm memberForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()){
            return "memberRegister"; // error 있으면(입력에 오류가 있다면) memberRegister.html 로 돌아가라 라는 뜻
        }

        
        // 회원가입 : db에 저장
        try {
            memberService.register(memberForm);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("가입실패", e.getMessage());
            redirectAttributes.addFlashAttribute("msg", "회원가입이 실패했습니다." + e.getMessage());
            return "memberRegister";
        }
        redirectAttributes.addFlashAttribute("msg", "회원가입이 완료되었습니다. 로그인을 해주세요");

        return "redirect:/login";
    }

    // 내 정보
    @GetMapping("/members/me")
    public String myPage(Authentication authentication, Model model) {
        System.out.println("Authentication: " + authentication);
        Member member = memberService.findByUsername(authentication.getName());
        model.addAttribute("member", member);
        model.addAttribute("memberForm", memberService.toUpdateForm(member));

        return "memberMyPage";  // memberMyPage.html
    }

    // 내 정보 수정
    @PostMapping("/members/me")
    public String updateMyPage(
            Authentication authentication,
            @Valid @ModelAttribute("memberForm") MemberUpdateForm memberForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        Member member = memberService.findByUsername(authentication.getName());

        if(bindingResult.hasErrors()) {
            model.addAttribute("member", member);
            return"memberMyPage";
        }
        try{
            memberService.update(member.getId(), memberForm, false);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("updateFail", e.getMessage());
            model.addAttribute("member", member);
            return "memberMyPage";
        }
        redirectAttributes.addFlashAttribute("msg", "내 정보가 수정되었습니다.");
        return "redirect:/members/me";
    }

}
