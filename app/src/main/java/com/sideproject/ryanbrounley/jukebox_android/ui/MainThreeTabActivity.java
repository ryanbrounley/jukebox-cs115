package com.sideproject.ryanbrounley.jukebox_android.ui;

import android.os.Bundle;

import com.sideproject.ryanbrounley.jukebox_android.FakeFragment;


public abstract class MainThreeTabActivity extends ThreeTabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    //Set all the tabs to set the content to the corresponding fragment
    //when they are clicked
    //Makes the three tabs point to the 3 top level intial fragments
    //the function is final so subclasses can not override it
    //If they wanted to override it they should just extend ThreeTabActivity
    @Override
    final protected void setUp() {
        setLeftTabListener(new OnClickChangeContent(new FakeFragment(), "Create")); //Recent is new main fragment 6/19/15
        setMiddleTabListener(new OnClickChangeContent(new FakeFragment(), "Find"));
        setRightTabListener(new OnClickChangeContent(new FakeFragment(), "Current"));
    }
}
