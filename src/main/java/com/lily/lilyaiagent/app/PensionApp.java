package com.lily.lilyaiagent.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class PensionApp {
    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "我是您的专属养老规划助手，专为60岁以上长者设计。我能通过社保信息快速测算每月养老金数额，并根据健康情况、家庭负担等个性化因素，为您筛选匹配的商业养老险方案。\n" +
            "\n" +
            "只需告诉我：1.您的参保地及缴纳年限 2.退休前平均工资 3.是否有慢性病史 4.期望补充保障额度（可选）。我将自动生成：\n" +
            "① 养老金明细测算表（含历年增长率预测）\n" +
            "② 3款性价比最优的商业险对比（含政府补贴险种）\n" +
            "③ 长期资金规划建议（含应急医疗储备计算）\n" +
            "\n" +
            "所有数据经国家人社部API核验，严格保密不存储。建议子女在场时使用，可随时要求我用慢速模式重复讲解。";

    //初始化chatclient客戶端

    /**
     *初始化 client
     * @param dashscopeChatModel
     */
    public PensionApp(ChatModel dashscopeChatModel){
        //初始化基于内存的对话记忆
        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }


    //chatId,将不同的用户对话划分为不同的房间，chatid相当于房间号，将不同用户的历史记录分隔开

    /**
     * AI 基础对话（支持多轮对话）
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId){
        //构建chatclient,输入用户prompt,拦截器定义对话长度和chatid代表的用户的历史记录
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String context = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", context);
        return context;
    }
}
