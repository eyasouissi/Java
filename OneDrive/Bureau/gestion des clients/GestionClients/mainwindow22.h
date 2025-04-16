#ifndef MAINWINDOW22_H
#define MAINWINDOW22_H

#include <QMainWindow>

QT_BEGIN_NAMESPACE
namespace Ui { class MainWindow22; }
QT_END_NAMESPACE

class MainWindow22 : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow22(QWidget *parent = nullptr);
    ~MainWindow22();

private:
    Ui::MainWindow22 *ui;
};
#endif // MAINWINDOW22_H
