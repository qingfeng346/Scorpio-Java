@title "Scorpio"
@set dir=%~dp0
@java -Xms256m -Xmx1024m -ea -cp %dir%/libs/*;%dir%/ScorpioExec.jar Scorpio.ScorpioExec %1%
@pause