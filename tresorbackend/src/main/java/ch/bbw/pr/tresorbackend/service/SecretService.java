package ch.bbw.pr.tresorbackend.service;

import ch.bbw.pr.tresorbackend.dto.*;
import ch.bbw.pr.tresorbackend.entities.User;
import ch.bbw.pr.tresorbackend.service.impl.CredentialSecretServiceImpl;
import ch.bbw.pr.tresorbackend.service.impl.CreditCardSecretServiceImpl;
import ch.bbw.pr.tresorbackend.service.impl.NoteSecretServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * SecretService
 * @author Peter Rutschmann
 */
@Service
public class SecretService {
   private final CreditCardSecretServiceImpl creditCardService;
   private final CredentialSecretServiceImpl credentialService;
   private final NoteSecretServiceImpl noteService;

    public SecretService(CreditCardSecretServiceImpl creditCardService, CredentialSecretServiceImpl credentialService, NoteSecretServiceImpl noteService) {
        this.creditCardService = creditCardService;
        this.credentialService = credentialService;
        this.noteService = noteService;
    }

    public List<SecretDto> getAllSecrets() {
       List<SecretDto> result = new ArrayList<>();

       result.addAll(creditCardService.getAllSecrets());
       result.addAll(credentialService.getAllSecrets());
       result.addAll(noteService.getAllSecrets());

       return result;
    }

   public List<SecretDto> getSecretsByUserId(Integer userId) {
      List<CreditCardSecretDto> creditCards = creditCardService.getSecretsByUserId(userId);
      List<CredentialSecretDto> credentials = credentialService.getSecretsByUserId(userId);
      List<NoteSecretDto> notes = noteService.getSecretsByUserId(userId);

      List<SecretDto> result = new ArrayList<>();

      result.addAll(creditCards);
      result.addAll(credentials);
      result.addAll(notes);

      return result;
   }

   public void createSecret(User user, SecretDto secretDto) {
       switch (secretDto.getType()) {
          case CREDENTIAL -> credentialService.createSecret((CredentialSecretDto) secretDto, user);
          case CREDIT_CARD -> creditCardService.createSecret((CreditCardSecretDto) secretDto, user);
          case NOTE -> noteService.createSecret((NoteSecretDto) secretDto, user);
       }
   }

   public SecretDto getSecretByIdAndUserId(SecretType type, Long secretId, Integer userId) {
       return switch (type) {
           case CREDENTIAL -> credentialService.getSecretByIdAndUserId(secretId, userId);
           case CREDIT_CARD -> creditCardService.getSecretByIdAndUserId(secretId, userId);
           case NOTE -> noteService.getSecretByIdAndUserId(secretId, userId);
       };
   }

   public SecretDto updateSecretByIdAndUserId(SecretType type, Long secretId, Integer userId, SecretDto secretRequest) {
      return switch (type) {
         case CREDENTIAL -> credentialService.updateSecretByIdAndUserId(secretId, userId, (CredentialSecretDto) secretRequest);
         case CREDIT_CARD -> creditCardService.updateSecretByIdAndUserId(secretId, userId, (CreditCardSecretDto) secretRequest);
         case NOTE -> noteService.updateSecretByIdAndUserId(secretId, userId, (NoteSecretDto) secretRequest);
      };
   }

   public void deleteSecretByIdAndUserId(SecretType type, Long secretId, Integer userId) {
       switch (type) {
          case CREDENTIAL -> credentialService.deleteSecretByIdAndUserId(secretId, userId);
          case CREDIT_CARD -> creditCardService.deleteSecretByIdAndUserId(secretId, userId);
          case NOTE -> noteService.deleteSecretByIdAndUserId(secretId, userId);
       }
   }
}
