# Quora

1. Build the project using the command "mvn clean install -DskipTests"
2. Update database by activating the profile setup using the commands
   * cd quora-db
   * mvn clean install -Psetup
   
3. Modify the database password in following files
   * quora-api\src\main\resources\application.yaml 
   * quora-db\src\main\resources\config\localhost.properties
4. While authenticating the user use the format "Basic base64encoded(username:password)"
   
# Contributors

1. UserController : Aditya Janardhanan
2. CommonController : Aproov Dubey
3. AdminController : Aproov Dubey
4. QuestionController: Sarjith R M
5. AnswerController : Ankur Sharma
