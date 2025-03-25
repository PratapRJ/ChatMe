import {onDocumentCreated} from "firebase-functions/v2/firestore";
import * as admin from "firebase-admin";
admin.initializeApp();

export const sendMessageNotification = onDocumentCreated(
  "Chats/{chatId}/Messages/{messageId}",
  async (event) => {
    const snapshot = event.data;
    const context = event.params;

    if (!snapshot) {
      console.log("No snapshot data");
      return;
    }

    const message = snapshot.data();
    const chatId = context.chatId;

    if (!message || message.userInChat) return null;

    const chatDoc = await admin.firestore()
      .collection("Chats")
      .doc(chatId)
      .get();
    const chatData = chatDoc.data();

    if (!chatData) return null;

    const senderId = message.userId;
    const recipientId = chatData.user1.userId === senderId ?
      chatData.user2.userId :
      chatData.user1.userId; // Fixed operator linebreak

    const recipientDoc = await admin.firestore()
      .collection("User")
      .doc(recipientId)
      .get();
    const recipientData = recipientDoc.data();
    const senderName = chatData.user1.userId === senderId ?
      chatData.user1.name :
      chatData.user2.name;


    if (!recipientData?.fcmToken || recipientData.online) return null;

    return admin.messaging().send({
      notification: {
        title: senderName || "Someone sent you a message",
        body: message.message || "You have a new message",
      },
      token: recipientData.fcmToken,
    });
  }
);
