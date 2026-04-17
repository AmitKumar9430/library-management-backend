package com.library.controller;

import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private BookRepository bookRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private LoanRepository loanRepository;

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return Map.of(
            "totalBooks",   bookRepository.count(),
            "totalMembers", memberRepository.count(),
            "activeLoans",  loanRepository.countByStatus("ACTIVE"),
            "returnedLoans",loanRepository.countByStatus("RETURNED")
        );
    }
}
