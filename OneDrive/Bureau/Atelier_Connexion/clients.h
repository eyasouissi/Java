#ifndef CLIENTS_H
#define CLIENTS_H
#include <QString>
#include<QSqlQueryModel>
#include <QTableView>
class Clients
{
public:
    Clients();
   Clients(int,QString,QString,QString,int);
    int getid();
    QString getnom();
    QString getprenom();
    QString getadresse();
    int  getnumero();
    void setid(int);
    void setnom(QString);
    void setprenom(QString);
    void setadresse(QString);
    void setnumero(int);
    bool ajouter();
    QSqlQueryModel* afficher();
    bool supprimer(int);
    bool exist();
    bool modifier(int);
    QSqlQueryModel*recherche(QString var);
        QSqlQueryModel * trier();
        void printPDF(const QList<Clients>& clientList);
        void resetAffichage(QTableView* tableView);

        //void printPDF();

private:
    int id,numero;
    QString nom,prenom,adresse;
};

#endif // CLIENTS_H
