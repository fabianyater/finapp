package com.fyr.finapp.adapters.driving.http;

import com.fyr.finapp.domain.api.account.AcceptInvitationUseCase;
import com.fyr.finapp.domain.api.account.DeclineInvitationUseCase;
import com.fyr.finapp.domain.api.account.ListPendingInvitationsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("${api.base-path}/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final ListPendingInvitationsUseCase listPendingInvitationsUseCase;
    private final AcceptInvitationUseCase acceptInvitationUseCase;
    private final DeclineInvitationUseCase declineInvitationUseCase;

    record InvitationResponse(String id, String accountId, String accountName,
                              String inviterName, String inviterEmail, Instant createdAt) {}

    @GetMapping("/pending")
    public ResponseEntity<List<InvitationResponse>> listPending() {
        var items = listPendingInvitationsUseCase.list();
        return ResponseEntity.ok(items.stream()
                .map(i -> new InvitationResponse(i.id(), i.accountId(), i.accountName(),
                        i.inviterName(), i.inviterEmail(), i.createdAt()))
                .toList());
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> accept(@PathVariable String id) {
        acceptInvitationUseCase.accept(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<Void> decline(@PathVariable String id) {
        declineInvitationUseCase.decline(id);
        return ResponseEntity.noContent().build();
    }
}
