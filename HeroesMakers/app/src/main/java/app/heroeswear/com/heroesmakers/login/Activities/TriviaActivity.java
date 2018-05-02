package app.heroeswear.com.heroesmakers.login.Activities;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import app.heroeswear.com.heroesmakers.R;
import app.heroeswear.com.heroesmakers.login.models.XmlQuestionEntry;

public class TriviaActivity extends AppCompatActivity implements View.OnClickListener
{
	private TextView questionTitle 	= null;
	private TextView firstAnswer 	= null;
	private TextView secondAnswer 	= null;
	private TextView thirdAnswer 	= null;
	private TextView fourthAnswer 	= null;

	private XmlPullParser               parser             = null;
	private ArrayList<XmlQuestionEntry> questionsEntries   = null;
	private int                         selectedEntryIndex = -1;
	private Random                      random             = null;
	private CountDownTimer              timer              = null;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trivia);

		random 			= new Random();

		questionTitle 	= (TextView) findViewById(R.id.question_title);
		firstAnswer 	= (TextView) findViewById(R.id.first_answer);
		secondAnswer 	= (TextView) findViewById(R.id.second_answer);
		thirdAnswer 	= (TextView) findViewById(R.id.third_answer);
		fourthAnswer 	= (TextView) findViewById(R.id.fourth_answer);

		firstAnswer.setOnClickListener(this);
		secondAnswer.setOnClickListener(this);
		thirdAnswer.setOnClickListener(this);
		fourthAnswer.setOnClickListener(this);

		parser 	= Xml.newPullParser();

		try
		{
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(getAssets().open("questions.xml"), null);
			questionsEntries = parseQuestionsXml(parser);
		}
		catch (XmlPullParserException  | IOException e)
		{	e.printStackTrace();	}

		if (questionsEntries != null)
			displayQuestions();
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();

		if (timer != null)
			timer.cancel();

		finish();
	}

	@Override
	public void onClick(View view)
	{
		if (selectedEntryIndex < 0 || selectedEntryIndex >= questionsEntries.size())
			return;

		XmlQuestionEntry entry 	= questionsEntries.get(selectedEntryIndex);

		switch (view.getId())
		{
			case R.id.first_answer:
				if (entry.correctAnswer == 0)
				{ 	correctAnswer(0); 	}
				else
					wrongAnswer(0);
				break;
			case R.id.second_answer:
				if (entry.correctAnswer == 1)
				{ 	correctAnswer(1); 	}
				else
					wrongAnswer(1);
				break;
			case R.id.third_answer:
				if (entry.correctAnswer == 2)
				{ 	correctAnswer(2); 	}
				else
					wrongAnswer(2);
				break;
			case R.id.fourth_answer:
				if (entry.correctAnswer == 3)
				{ 	correctAnswer(3); 	}
				else
					wrongAnswer(3);
				break;
			default:
				break;
		}
	}

	private ArrayList<XmlQuestionEntry> parseQuestionsXml(XmlPullParser parser) throws XmlPullParserException,IOException
	{
		ArrayList<XmlQuestionEntry> entries   = null;
		int                 		eventType = parser.getEventType();
		XmlQuestionEntry    		entry     = null;

		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			String name;

			switch (eventType)
			{
				case XmlPullParser.START_DOCUMENT:
					entries = new ArrayList<>();
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();

					if (name.equals("QuestionId"))
					{
						entry 			= new XmlQuestionEntry();
						entry.questionId = Integer.valueOf(parser.nextText());
					}
					else if (entry != null && name.equals("QuestionText"))
					{	entry.questionText = parser.nextText();						}
					else if (entry != null && name.equals("FirstAnswer"))
					{	entry.firstAnswer = parser.nextText();						}
					else if (entry != null && name.equals("SecondAnswer"))
					{	entry.secondAnswer = parser.nextText();						}
					else if (entry != null && name.equals("ThirdAnswer"))
					{	entry.thirdAnswer = parser.nextText();						}
					else if (entry != null && name.equals("FourthAnswer"))
					{	entry.fourthAnswer = parser.nextText();						}
					else if (entry != null && name.equals("CorrectAnswer"))
					{	entry.correctAnswer = Integer.valueOf(parser.nextText());	}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();

					if (name.equalsIgnoreCase("record") && entries != null && entry != null)
					{
						entries.add(entry);
						entry = null;
					}

					break;
				default:
					break;
			}

			eventType = parser.next();
		}

		return entries;
	}

	private void displayQuestions()
	{
		selectedEntryIndex 		= random.nextInt(questionsEntries.size());
		XmlQuestionEntry entry 	= questionsEntries.get(selectedEntryIndex);

		if (entry.questionText != null && entry.questionText.isEmpty() == false)
			questionTitle.setText	(entry.questionText);

		if (entry.firstAnswer != null && entry.firstAnswer.isEmpty() == false)
		{
			firstAnswer.setText		(entry.firstAnswer);
			firstAnswer.setBackgroundResource(R.drawable.shape_ellipse_button_light_blue);
			firstAnswer.setVisibility(View.VISIBLE);
		}
		else
			firstAnswer.setVisibility(View.GONE);

		if (entry.secondAnswer != null && entry.secondAnswer.isEmpty() == false)
		{
			secondAnswer.setText	(entry.secondAnswer);
			secondAnswer.setBackgroundResource(R.drawable.shape_ellipse_button_light_blue);
			secondAnswer.setVisibility(View.VISIBLE);
		}
		else
			secondAnswer.setVisibility(View.GONE);

		if (entry.thirdAnswer != null && entry.thirdAnswer.isEmpty() == false)
		{
			thirdAnswer.setText		(entry.thirdAnswer);
			thirdAnswer.setBackgroundResource(R.drawable.shape_ellipse_button_light_blue);
			thirdAnswer.setVisibility(View.VISIBLE);
		}
		else
			thirdAnswer.setVisibility(View.GONE);

		if (entry.fourthAnswer != null && entry.fourthAnswer.isEmpty() == false)
		{
			fourthAnswer.setText	(entry.fourthAnswer);
			fourthAnswer.setBackgroundResource(R.drawable.shape_ellipse_button_light_blue);
			fourthAnswer.setVisibility(View.VISIBLE);
		}
		else
			fourthAnswer.setVisibility(View.GONE);
	}

	private void correctAnswer(int answerIndex)
	{
		switch (answerIndex)
		{
			case 0:
				firstAnswer.setBackgroundResource(R.drawable.shape_ellipse_button_green);
				break;
			case 1:
				secondAnswer.setBackgroundResource(R.drawable.shape_ellipse_button_green);
				break;
			case 2:
				thirdAnswer.setBackgroundResource(R.drawable.shape_ellipse_button_green);
				break;
			case 3:
				fourthAnswer.setBackgroundResource(R.drawable.shape_ellipse_button_green);
				break;
			default:
				return;
		}

		countDownAndShowNextQuestion();
	}

	private void wrongAnswer(int answerIndex)
	{
		switch (answerIndex)
		{
			case 0:
				firstAnswer.setBackgroundResource(R.drawable.shape_ellipse_button_red);
				break;
			case 1:
				secondAnswer.setBackgroundResource(R.drawable.shape_ellipse_button_red);
				break;
			case 2:
				thirdAnswer.setBackgroundResource(R.drawable.shape_ellipse_button_red);
				break;
			case 3:
				fourthAnswer.setBackgroundResource(R.drawable.shape_ellipse_button_red);
				break;
			default:
				return;
		}

		countDownAndShowNextQuestion();
	}

	private void countDownAndShowNextQuestion()
	{
		timer = new CountDownTimer(2000, 100)
		{
			public void onTick(long millisUntilFinished)
			{
				/*int secondsUntilFinished = Math.round((float)millisUntilFinished / 1000.0f);

				if (secondsUntilFinished != secondsLeft[0])
				{
					secondsLeft[0] = secondsUntilFinished;
					digit.setText(String.valueOf(secondsLeft[0]));
				}*/
			}

			@Override
			public void onFinish()
			{
				displayQuestions();
			}
		};
		timer.start();
	}
}