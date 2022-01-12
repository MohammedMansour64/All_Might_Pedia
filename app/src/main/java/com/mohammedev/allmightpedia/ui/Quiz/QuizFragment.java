package com.mohammedev.allmightpedia.ui.Quiz;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.Question;
import com.mohammedev.allmightpedia.databinding.FragmentQuizBinding;
import com.mohammedev.allmightpedia.databinding.QuizQuestionLayoutBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class QuizFragment extends Fragment {

    private QuizViewModel mViewModel;
    private QuizQuestionLayoutBinding bindingSecond;
    private FragmentQuizBinding binding;
    private ArrayList<Question> questionsArray = new ArrayList<>();
    private ArrayList<String> answersArray = new ArrayList<>();
    public Button one, two, three, four;
    public TextView questionTitle, questionCounter;
    public int currentQuestion;
    public int numberCorrectAnswers;
    int nextQuestion;
    public String correctAnswer;
    int oneClick = 0,twoClick = 0,threeClick = 0,fourClick = 0;
    ProgressBar progressBar;

    public static QuizFragment newInstance() {
        return new QuizFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentQuizBinding.inflate(inflater, container, false);
        bindingSecond = QuizQuestionLayoutBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        one = binding.included.answerOneBtn;
        two = binding.included.answerTwoBtn;
        three = binding.included.answerThreeBtn;
        four = binding.included.answerFourBtn;
        questionTitle = binding.included.questionTxt;
        questionCounter = binding.included.questionCounter;
        progressBar = binding.progressBar;

        binding.startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                fetchQuizData();
            }
        });

        one.setOnClickListener(v -> {
            one.setBackgroundColor(Color.BLUE);
            two.setBackgroundColor(getResources().getColor(R.color.yellowOne));
            three.setBackgroundColor(getResources().getColor(R.color.yellowOne));
            four.setBackgroundColor(getResources().getColor(R.color.yellowOne));
            oneClick = 1;
            twoClick = 0;
            threeClick = 0;
            fourClick = 0;
        });

        two.setOnClickListener(v -> {
            two.setBackgroundColor(Color.BLUE);
            one.setBackgroundColor(getResources().getColor(R.color.yellowOne));
            three.setBackgroundColor(getResources().getColor(R.color.yellowOne));
            four.setBackgroundColor(getResources().getColor(R.color.yellowOne));
            oneClick = 0;
            twoClick = 1;
            threeClick = 0;
            fourClick = 0;
        });

        three.setOnClickListener(v -> {
            three.setBackgroundColor(Color.BLUE);
            one.setBackgroundColor(getResources().getColor(R.color.yellowOne));
            two.setBackgroundColor(getResources().getColor(R.color.yellowOne));
            four.setBackgroundColor(getResources().getColor(R.color.yellowOne));
            oneClick = 0;
            twoClick = 0;
            threeClick = 1;
            fourClick = 0;
        });

        four.setOnClickListener(v -> {
            four.setBackgroundColor(Color.BLUE);
            two.setBackgroundColor(getResources().getColor(R.color.yellowOne));
            three.setBackgroundColor(getResources().getColor(R.color.yellowOne));
            one.setBackgroundColor(getResources().getColor(R.color.yellowOne));
            oneClick = 0;
            twoClick = 0;
            threeClick = 0;
            fourClick = 1;
        });


        binding.included.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        // TODO: Use the ViewModel
    }

    public void fetchQuizData(){

        binding.textView4.setVisibility(View.GONE);
        binding.startQuiz.setVisibility(View.GONE);


        Query query = FirebaseDatabase.getInstance().getReference().child("quiz");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot quiz: snapshot.getChildren()){
                    Question question = quiz.getValue(Question.class);
                    questionsArray.add(question);
                }
                System.out.println(questionsArray.size());
                progressBar.setVisibility(View.GONE);
                binding.included.getRoot().setVisibility(View.VISIBLE);
                sequenceTheQuiz();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("There was an error:" + error.getDetails());
            }
        });

    }

    private void sequenceTheQuiz(){
        if (questionsArray != null){
            Collections.shuffle(questionsArray);
            currentQuestion = 0;
            answersArray.add(questionsArray.get(currentQuestion).getAnswerOne());
            answersArray.add(questionsArray.get(currentQuestion).getAnswerTwo());
            answersArray.add(questionsArray.get(currentQuestion).getAnswerThree());
            answersArray.add(questionsArray.get(currentQuestion).getAnswerFour());
            Collections.shuffle(answersArray);
            correctAnswer = questionsArray.get(currentQuestion).getCorrectAnswer();
            questionCounter.setText(String.valueOf(currentQuestion+1) +"/"+ questionsArray.size());
            questionTitle.setText(questionsArray.get(currentQuestion).getQuestionTitle());
            one.setText(answersArray.get(0));
            two.setText(answersArray.get(1));
            three.setText(answersArray.get(2));
            four.setText(answersArray.get(3));


        }
    }
    private void nextQuestion(){
        answersArray.clear();
        if (questionsArray != null) {
            if (oneClick == 0 && twoClick == 0 && threeClick == 0 && fourClick == 0) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.please_answer, Toast.LENGTH_SHORT).show();
            }else {
                if (oneClick == 1) {
                    if (one.getText().toString().equals(correctAnswer)) {
                        numberCorrectAnswers++;
                        System.out.println(numberCorrectAnswers);
                    }
                }

                if (twoClick == 1) {

                    if (two.getText().toString().equals(correctAnswer)) {
                        numberCorrectAnswers++;
                        System.out.println(numberCorrectAnswers);
                    }
                }

                if (threeClick == 1) {
                    if (three.getText().toString().equals(correctAnswer)) {
                        numberCorrectAnswers++;
                        System.out.println(numberCorrectAnswers);
                    }
                }

                if (fourClick == 1) {
                    if (four.getText().toString().equals(correctAnswer)) {
                        numberCorrectAnswers++;
                        System.out.println(numberCorrectAnswers);
                    }
                }

                oneClick = 0;
                twoClick = 0;
                threeClick = 0;
                fourClick = 0;
                one.setBackgroundColor(getResources().getColor(R.color.yellowOne));
                two.setBackgroundColor(getResources().getColor(R.color.yellowOne));
                three.setBackgroundColor(getResources().getColor(R.color.yellowOne));
                four.setBackgroundColor(getResources().getColor(R.color.yellowOne));

                nextQuestion = currentQuestion += 1;
                answersArray.add(questionsArray.get(nextQuestion).getAnswerOne());
                answersArray.add(questionsArray.get(nextQuestion).getAnswerTwo());
                answersArray.add(questionsArray.get(nextQuestion).getAnswerThree());
                answersArray.add(questionsArray.get(nextQuestion).getAnswerFour());
                Collections.shuffle(answersArray);


                correctAnswer = questionsArray.get(nextQuestion).getCorrectAnswer();
                questionCounter.setText(String.valueOf(nextQuestion + 1) + "/" + questionsArray.size());
                questionTitle.setText(questionsArray.get(nextQuestion).getQuestionTitle());
                one.setText(answersArray.get(0));
                two.setText(answersArray.get(1));
                three.setText(answersArray.get(2));
                four.setText(answersArray.get(3));

            }
        }
    }

}