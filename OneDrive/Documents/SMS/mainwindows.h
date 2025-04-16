#ifndef MAINWINDOWS_H
#define MAINWINDOWS_H

#include <QMainWindow>

QT_BEGIN_NAMESPACE
namespace Ui { class MainWindowS; }
QT_END_NAMESPACE

class MainWindowS : public QMainWindow
{
    Q_OBJECT

public:
    MainWindowS(QWidget *parent = nullptr);
    ~MainWindowS();

private:
    Ui::MainWindowS *ui;
};
#endif // MAINWINDOWS_H
