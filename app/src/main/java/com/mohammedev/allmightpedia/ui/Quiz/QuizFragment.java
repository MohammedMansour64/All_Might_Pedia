package com.mohammedev.allmightpedia.ui.Quiz;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.Random;

public class QuizFragment extends Fragment {

    private QuizViewModel mViewModel;
    private QuizQuestionLayoutBinding bindingSecond;
    private FragmentQuizBinding binding;
    private ArrayList<Question> questionsArray = new ArrayList<>();

    public static QuizFragment newInstance() {
        return new QuizFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentQuizBinding.inflate(inflater, container, false);
        bindingSecond = QuizQuestionLayoutBinding.inflate(inflater, container, false);

        View root = binding.getRoot();


        binding.startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textView4.setVisibility(View.GONE);
                binding.startQuiz.setVisibility(View.GONE);
                binding.included.getRoot().setVisibility(View.VISIBLE);
                fetchQuizData();
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
        Query query = FirebaseDatabase.getInstance().getReference().child("quiz");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot quiz: snapshot.getChildren()){
                    Question question = quiz.getValue(Question.class);
                    questionsArray.add(question);
                }
                System.out.println(questionsArray.size());
                sequenceTheQuiz();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("There was an error:" + error.getDetails());
            }
        });

    }

    private void sequenceTheQuiz(){
        boolean correct = false;
        if (questionsArray != null){
            for (int i = 0; i <= questionsArray.size(); i++){
                if (questionsArray.size() > 5){
                    Random random = new Random();
                    int x = random.nextInt(questionsArray.size());
                    questionsArray.remove(x);
                }
            }
            if (questionsArray.size() == 5){
                System.out.println(questionsArray.size());
                Random random = new Random();
                int x = random.nextInt(questionsArray.size());
                System.out.println(x);

                binding.included.questionTxt.setText(questionsArray.get(x).getQuestionTitle());
                binding.included.answerOneBtn.setText(questionsArray.get(x).getAnswerOne());
                binding.included.answerTwoBtn.setText(questionsArray.get(x).getAnswerTwo());
                binding.included.answerThreeBtn.setText(questionsArray.get(x).getAnswerThree());
                binding.included.answerFourBtn.setText(questionsArray.get(x).getAnswerFour());
                System.out.println(x);
                if (binding.included.answerOneBtn.getText() == questionsArray.get(x).getCorrectAnswer()){
                    binding.included.answerOneBtn.setBackgroundColor(Color.GREEN);
                    correct = true;
                }else if (binding.included.answerTwoBtn.getText() == questionsArray.get(x).getCorrectAnswer()){
                    binding.included.answerTwoBtn.setBackgroundColor(Color.GREEN);
                    correct = true;
                }else if (binding.included.answerThreeBtn.getText() == questionsArray.get(x).getCorrectAnswer()){
                    binding.included.answerThreeBtn.setBackgroundColor(Color.GREEN);
                    correct = true;
                }else if (binding.included.answerFourBtn.getText() == questionsArray.get(x).getCorrectAnswer()){
                    binding.included.answerThreeBtn.setBackgroundColor(Color.GREEN);
                    correct = true;
                    System.out.println(x);
                }else{
                    System.out.println(x);
                }

            }else{
                System.out.println(questionsArray.size() + "hi");
            }


        }
    }
}