package com.library.controller;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired private LoanRepository loanRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private MemberRepository memberRepository;

    @GetMapping
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long id) {
        return loanRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/member/{memberId}")
    public List<Loan> getLoansByMember(@PathVariable Long memberId) {
        return loanRepository.findByMemberId(memberId);
    }

    @GetMapping("/active")
    public List<Loan> getActiveLoans() {
        return loanRepository.findByStatus("ACTIVE");
    }

    @PostMapping
    public ResponseEntity<?> createLoan(@RequestBody Map<String, Object> payload) {
        Long bookId = Long.valueOf(payload.get("bookId").toString());
        Long memberId = Long.valueOf(payload.get("memberId").toString());

        Book book = bookRepository.findById(bookId).orElse(null);
        Member member = memberRepository.findById(memberId).orElse(null);

        if (book == null) return ResponseEntity.badRequest().body(Map.of("message", "Book not found"));
        if (member == null) return ResponseEntity.badRequest().body(Map.of("message", "Member not found"));
        if (book.getAvailableQuantity() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "No copies available"));
        }

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setStatus("ACTIVE");

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.save(book);

        return ResponseEntity.ok(loanRepository.save(loan));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long id) {
        return loanRepository.findById(id).map(loan -> {
            loan.setReturnDate(LocalDate.now());
            loan.setStatus("RETURNED");

            Book book = loan.getBook();
            book.setAvailableQuantity(book.getAvailableQuantity() + 1);
            bookRepository.save(book);

            return ResponseEntity.ok(loanRepository.save(loan));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        if (!loanRepository.existsById(id)) return ResponseEntity.notFound().build();
        loanRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
