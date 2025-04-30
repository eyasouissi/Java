package tn.esprit.services;

import tn.esprit.entities.Offre;

import java.sql.SQLException;
import java.util.List;

<<<<<<< Updated upstream
public interface IServices <T>{
    void ajouter(T t) throws SQLException;
    void supprimer(T t) throws SQLException ;
    public void modifier(int id, T t) throws SQLException;
    List<T> recuperer() throws SQLException;
=======
public interface
IServices<T>  {
    void ajouter(T t);
    void modifier(T t);
    void supprimer(int id);


    T getOne(T t);
    List<T> getAll();
>>>>>>> Stashed changes
}
