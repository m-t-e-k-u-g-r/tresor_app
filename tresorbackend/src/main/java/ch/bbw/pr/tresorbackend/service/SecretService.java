package ch.bbw.pr.tresorbackend.service;

import ch.bbw.pr.tresorbackend.dto.*;
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

   public void createSecret(SecretType type, CreateSecretRequest<? extends SecretDto> secretRequest) {
       switch (type) {
          case CREDENTIAL -> credentialService.createSecret((CreateSecretRequest<CredentialSecretDto>) secretRequest);
          case CREDIT_CARD -> creditCardService.createSecret((CreateSecretRequest<CreditCardSecretDto>) secretRequest);
          case NOTE -> noteService.createSecret((CreateSecretRequest<NoteSecretDto>) secretRequest);
       }
   }

   public SecretDto getSecretById(SecretType type, Long secretId) {
       return switch (type) {
           case CREDENTIAL -> credentialService.getSecretById(secretId);
           case CREDIT_CARD -> creditCardService.getSecretById(secretId);
           case NOTE -> noteService.getSecretById(secretId);
       };
   }

   public SecretDto updateSecret(SecretType type, Long secretId, SecretDto secretRequest) {
      return switch (type) {
         case CREDENTIAL -> credentialService.updateSecret(secretId, (CredentialSecretDto) secretRequest);
         case CREDIT_CARD -> creditCardService.updateSecret(secretId, (CreditCardSecretDto) secretRequest);
         case NOTE -> noteService.updateSecret(secretId, (NoteSecretDto) secretRequest);
      };
   }

   public void deleteSecret(SecretType type, Long secretId) {
       switch (type) {
          case CREDENTIAL -> credentialService.deleteSecret(secretId);
          case CREDIT_CARD -> creditCardService.deleteSecret(secretId);
          case NOTE -> noteService.deleteSecret(secretId);
       }
   }
}
