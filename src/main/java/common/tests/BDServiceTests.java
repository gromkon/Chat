package common.tests;

import server.utils.DBService;

public class BDServiceTests {

    public static void main(String[] args) {
        DBService.connect();

//        System.out.println(DBService.isUser("user1"));
//        System.out.println(DBService.isUser("user2"));
//        System.out.println(DBService.isUser("user3"));
//        System.out.println(DBService.isUser("user4"));
//        System.out.println(DBService.isUser("user5"));

//        System.out.println(DBService.isPassword("user1", "pass1"));
//        System.out.println(DBService.isPassword("user2", "pass4"));
//        System.out.println(DBService.isPassword("user3", "pass3"));
//        System.out.println(DBService.isPassword("user4", "pass2"));
//        System.out.println(DBService.isPassword("user12", "pass1"));
//        System.out.println(DBService.isPassword("user2", "pass44"));
//        System.out.println(DBService.isPassword("user", "pass43"));
//        System.out.println(DBService.isPassword("user4", "pass232"));

//        System.out.println(DBService.setStatus("user1", 0));
//        System.out.println(DBService.setStatus("user2", 0));
//        System.out.println(DBService.setStatus("user3", 0));
//        System.out.println(DBService.setStatus("user5", 0));

//        System.out.println(DBService.isUserOnline("nick1"));
//        System.out.println(DBService.isUserOnline("nick2"));
//        System.out.println(DBService.isUserOnline("nick3"));
//        System.out.println(DBService.isUserOnline("nick1231231"));

//        System.out.println(DBService.isRegLogin("user1"));
//        System.out.println(DBService.isRegLogin("user2"));
//        System.out.println(DBService.isRegLogin("user3"));
//        System.out.println(DBService.isRegLogin("user4"));
//        System.out.println(DBService.isRegLogin("user43"));
//        System.out.println(DBService.isRegLogin("user41"));
//        System.out.println(DBService.isRegLogin("use"));
//        System.out.println(DBService.isRegLogin("user"));

//        System.out.println(DBService.isRegNickname("nick1"));
//        System.out.println(DBService.isRegNickname("nick2"));
//        System.out.println(DBService.isRegNickname("nick3"));
//        System.out.println(DBService.isRegNickname("nick4"));
//        System.out.println(DBService.isRegNickname("nick"));
//        System.out.println(DBService.isRegNickname("nick11"));
//        System.out.println(DBService.isRegNickname("nick1231"));
//        System.out.println(DBService.isRegNickname("nickasd1"));

//        System.out.println(DBService.reg("nick1", "user5", "pass5").answer);
//        System.out.println(DBService.reg("nick5", "user1", "pass5").answer);
//        System.out.println(DBService.reg("nick5", "user5", "pass5").answer);
//        System.out.println(DBService.reg("nick6", "user6", "pass5").answer);


//        System.out.println(DBService.addChatMessage(1, 2, "Привет от пользователя 1!"));
//        System.out.println(DBService.addChatMessage(1, 2, "Привет от пользователя 2!"));
//
//        System.out.println(DBService.addSystemMessage(3, "Привет от пользователя 3!"));
//        System.out.println(DBService.addSystemMessage(4, "Привет от пользователя 4!"));

//        System.out.println(DBService.newChat("chat1"));
//        System.out.println(DBService.newChat("chat2"));
//        System.out.println(DBService.newChat("chat4"));

//        System.out.println(DBService.checkUserInChat("1", "1"));
//        System.out.println(DBService.checkUserInChat("2", "15"));
//        System.out.println(DBService.checkUserInChat("3", "11"));
//        System.out.println(DBService.checkUserInChat("4", "6"));
//        System.out.println(DBService.checkUserInChat("1", "2"));
//        System.out.println(DBService.checkUserInChat("1", "6"));

//        System.out.println(DBService.addUserToChat("1", "1").answer);
//        System.out.println(DBService.addUserToChat("1", "6").answer);
//        System.out.println(DBService.addUserToChat("1", "8").answer);
//        System.out.println(DBService.addUserToChat("2", "3").answer);

//        System.out.println(DBService.getNicknameByLoginAndPass("user1", "pass1"));
//        System.out.println(DBService.getNicknameByLoginAndPass("user5", "pass5"));
//        System.out.println(DBService.getNicknameByLoginAndPass("user6", "pass5"));
//        System.out.println(DBService.getNicknameByLoginAndPass("user1", "pass12"));

//        System.out.println(DBService.getUserChatList("3"));
//        System.out.println(DBService.getUserChatList("1"));
//        System.out.println(DBService.getUserChatList("6"));

        System.out.println(DBService.getChatIdByChatNameAndUserId("Общий", "0"));
        System.out.println(DBService.getChatIdByChatNameAndUserId("Общий", "1"));


        DBService.disconnect();
    }

}