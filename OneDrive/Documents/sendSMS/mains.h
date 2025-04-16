#ifndef MAINS_H
#define MAINS_H

#include <QMainWindow>

QT_BEGIN_NAMESPACE
namespace Ui { class MainS; }
QT_END_NAMESPACE

class MainS : public QMainWindow
{
    Q_OBJECT

public:
    MainS(QWidget *parent = nullptr);
    ~MainS();

private:
    Ui::MainS *ui;
};
#endif // MAINS_H
