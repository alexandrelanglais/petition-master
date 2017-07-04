package fr.demandeatonton.petition.model;

import java.util.Optional;

public class PetitionSigner {
   private String name;
   private String email;
   private Optional<String> comment;

   public PetitionSigner(String name, String email, Optional<String> comment) {
      this.name = name;
      this.email = email;
      this.comment = comment;
   }

   public PetitionSigner(String name, String email) {
      this(name, email, Optional.empty());
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public Optional<String> getComment() {
      return comment;
   }

   public void setComment(Optional<String> comment) {
      this.comment = comment;
   }

}
