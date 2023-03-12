rd /s/q "../attach-agent/target"
cd "./target"
del "attach-agent-1.0.0.jar"
cd "../../attach-agent/"
call mvn clean package
cd "./target"
copy "attach-agent-1.0.0.jar" "../../proxy/target"
pause