package app.heroeswear.com.heroesmakers.login.Activities;

import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import app.heroeswear.com.heroesmakers.R;
import app.heroeswear.com.heroesmakers.login.trivias.XmlQuestionEntry;

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

		questionTitle.setText	(entry.questionText);
		firstAnswer.setText		(entry.firstAnswer);
		secondAnswer.setText	(entry.secondAnswer);
		thirdAnswer.setText		(entry.thirdAnswer);
		fourthAnswer.setText	(entry.fourthAnswer);

		firstAnswer.setBackgroundResource(R.drawable.shape_ellipse_button_gray);
		secondAnswer.setBackgroundResource(R.drawable.shape_ellipse_button_gray);
		thirdAnswer.setBackgroundResource(R.drawable.shape_ellipse_button_gray);
		fourthAnswer.setBackgroundResource(R.drawable.shape_ellipse_button_gray);
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