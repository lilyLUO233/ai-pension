package com.lily.lilyaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
/***
 * 当项目启动时，spring会扫描component注解的bean,发现SpringAiAiInvoke有实现commandLineRunner接口，
 * 就会执行run方法，我们就能在项目启动时，自动注入依赖，并且调用ai大模型
 * ***/
@Component
public class SpringAiAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel;


    @Override
    public void run(String... args) throws Exception {
        AssistantMessage assistantMessage = dashscopeChatModel.call(new Prompt("你好，我是lily"))
                .getResult()
                .getOutput();
        System.out.println(assistantMessage.getText());
    }
}
