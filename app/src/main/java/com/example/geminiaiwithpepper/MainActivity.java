package com.example.geminiaiwithpepper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;


import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;

import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.example.geminiaiwithpepper.databinding.ActivityMainBinding;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private ActivityMainBinding binding;

    // Access your API key as a Build Configuration variable
    String apiKey="AIzaSyCNBhyyPo2G8vhocB7AqxLaP_J28oL7WB0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       QiSDK.register(this, this);


    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        listenAndWrite(qiContext);
    }

    @Override
    public void onRobotFocusLost() {

    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }

    @Override
    protected void onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    public void listenAndWrite(QiContext qiContext){


        // Create a topic
        Topic topic = TopicBuilder.with(qiContext)
                .withResource(R.raw.shop)
                .build();

// Create a QiChatbot
        QiChatbot qichatbot = QiChatbotBuilder.with(qiContext)
                .withTopic(topic)
                .build();
        // Create a Chat
        Chat chat = ChatBuilder.with(qiContext).withChatbot(qichatbot).build();



        chat.addOnHeardListener(heardPhrase -> {
            // Called when a phrase was recognized.
            generetaModel(heardPhrase.getText());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //binding.text.setText(heardPhrase.getText());

                }

            });
        });
        chat.async().run();

    }

    public  void generetaModel(String str){
        GenerativeModel gm = new GenerativeModel( "gemini-1.5-flash", apiKey);

        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText(str)
                .build();

        Executor executor = Executors.newSingleThreadExecutor(); ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            executor = this.getMainExecutor();
        }
        Log.e("armut" ,"dsadasdsa ");
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {

            @Override
            public void onSuccess(GenerateContentResponse result) {
                Log.e("armut" ,"hata yok canım  " );
                String resultText = result.getText();
               runOnUiThread(() -> binding.text.setText(resultText));

               // System.out.println(resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("armut" ,"hata burda sebebi " +t.getMessage());
            }
        },executor);
    }

}