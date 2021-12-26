package com.mohammedev.allmightpedia.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mohammedev.allmightpedia.R;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> quote;
    private MutableLiveData<String> description;

    public HomeViewModel() {
        quote = new MutableLiveData<>();
        description = new MutableLiveData<>();
        quote.setValue("\"Have No fear. Why? Because I am here!\"");
        description.setValue("Toshinori Yagi, more commonly known by his hero name, All Might, is the tritagonist of My Hero Academia, and the arc protagonist of the Hideout Raid Arc. All Might is the former No. 1 Pro Hero who bore the title of the world's Symbol of Peace. He teaches Foundational Hero Studies at U.A. High School.\n" +
                "All Might was the eighth holder of the One For All Quirk after receiving it from Nana Shimura. He has since passed the torch to Izuku Midoriya, whom he is training to be his successor. After using up all the embers of One For All to defeat All For One, All Might retired and ended his era as the world's greatest hero.");
    }

    public LiveData<String> getQuote() {
        return quote;
    }

    public LiveData<String> getDescription() {
        return description;
    }
}