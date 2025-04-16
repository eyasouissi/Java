#ifndef MAINSMS_H
#define MAINSMS_H

#include <QMainWindow>

QT_BEGIN_NAMESPACE
namespace Ui { class MainSms; }
QT_END_NAMESPACE

class MainSms : public QMainWindow
{
    Q_OBJECT

public:
    MainSms(QWidget *parent = nullptr);
    ~MainSms();

private:
    Ui::MainSms *ui;
};
#endif // MAINSMS_H
