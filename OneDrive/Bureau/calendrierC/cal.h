#ifndef CAL_H
#define CAL_H

#include <QMainWindow>

QT_BEGIN_NAMESPACE
namespace Ui { class cal; }
QT_END_NAMESPACE

class cal : public QMainWindow
{
    Q_OBJECT

public:
    cal(QWidget *parent = nullptr);
    ~cal();

private:
    Ui::cal *ui;
};
#endif // CAL_H
