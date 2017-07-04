package fr.demandeatonton.petition.model;

import java.util.ArrayList;
import java.util.List;

public class Petition {
   private int id;
   private String name;
   private String author;
   private int goal;
   private List<PetitionSigner> signers;

   public Petition() {
      super();
   }

   public Petition(String name, String author, int goal) {
      this.name = name;
      this.author = author;
      this.goal = goal;
      this.signers = new ArrayList<>();
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getAuthor() {
      return author;
   }

   public void setAuthor(String author) {
      this.author = author;
   }

   public int getGoal() {
      return goal;
   }

   public void setGoal(int goal) {
      this.goal = goal;
   }

   public List<PetitionSigner> getSigners() {
      return signers;
   }

   public void setSigners(List<PetitionSigner> signers) {
      this.signers = signers;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((author == null) ? 0 : author.hashCode());
      result = prime * result + goal;
      result = prime * result + id;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Petition other = (Petition) obj;
      if (author == null) {
         if (other.author != null)
            return false;
      } else if (!author.equals(other.author))
         return false;
      if (goal != other.goal)
         return false;
      if (id != other.id)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

}
