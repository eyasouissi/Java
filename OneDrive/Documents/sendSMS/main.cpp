#include "mains.h"

#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    MainS w;
    w.show();
    return a.exec();
}
