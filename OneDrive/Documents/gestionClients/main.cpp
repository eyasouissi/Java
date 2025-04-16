#include "clients.h"

#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    Clients w;
    w.show();
    return a.exec();
}
