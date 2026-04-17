package com.library.controller;

import com.library.model.Member;
import com.library.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        return memberRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Member> searchMembers(@RequestParam(required = false) String name) {
        if (name != null) return memberRepository.findByNameContainingIgnoreCase(name);
        return memberRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email already registered"));
        }
        return ResponseEntity.ok(memberRepository.save(member));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id,
                                           @Valid @RequestBody Member updated) {
        return memberRepository.findById(id).map(member -> {
            member.setName(updated.getName());
            member.setEmail(updated.getEmail());
            member.setPhone(updated.getPhone());
            member.setAddress(updated.getAddress());
            member.setActive(updated.isActive());
            return ResponseEntity.ok(memberRepository.save(member));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        if (!memberRepository.existsById(id)) return ResponseEntity.notFound().build();
        memberRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
