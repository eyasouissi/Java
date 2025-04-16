#ifndef MAINWINDOWLOGIN_H
#define MAINWINDOWLOGIN_H

#include <QMainWindow>

QT_BEGIN_NAMESPACE
namespace Ui { class MainWindowLogin; }
QT_END_NAMESPACE

class MainWindowLogin : public QMainWindow
{
    Q_OBJECT

public:
    MainWindowLogin(QWidget *parent = nullptr);
    ~MainWindowLogin();

private:
    Ui::MainWindowLogin *ui;
};
#endif // MAINWINDOWLOGIN_H
