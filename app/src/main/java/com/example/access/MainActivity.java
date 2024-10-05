package com.example.access;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView resultTv, solutionTv;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        playOpeningSound();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        resultTv = findViewById(R.id.result_tv);
        solutionTv = findViewById(R.id.solution_tv);

        int[] buttonIds = {
                R.id.btn_c, R.id.btn_del, R.id.btn_percent, R.id.btn_divide,
                R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_multiply,
                R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_add,
                R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_subtract,
                R.id.btn_point, R.id.btn_0, R.id.btn_equal
        };

        for (int id : buttonIds) {
            ImageButton button = findViewById(id);
            button.setOnClickListener(this);
        }
    }

    private void playOpeningSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.opening_music);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onClick(View view) {
        ImageButton button = (ImageButton) view;
        String buttonTag = (String) button.getTag();
        String currentInput = solutionTv.getText().toString();

        vibrateOnClick();

        switch (buttonTag) {
            case "C":
                clearData();
                break;
            case "⌫":
                deleteLastCharacter(currentInput);
                break;
            case "=":
                calculateResult(currentInput);
                break;
            default:
                handleInput(buttonTag, currentInput);
                break;
        }
    }

    private void clearData() {
        solutionTv.setText("");
        resultTv.setText("0");
    }

    private void deleteLastCharacter(String currentInput) {
        if (!currentInput.isEmpty()) {
            solutionTv.setText(currentInput.substring(0, currentInput.length() - 1));
        }
    }

    private void calculateResult(String currentInput) {
        String result = getResult(currentInput);
        resultTv.setText(result);
    }

    private void handleInput(String buttonTag, String currentInput) {
        if (isOperator(buttonTag)) {
            if (!currentInput.isEmpty()) {
                char lastChar = currentInput.charAt(currentInput.length() - 1);
                if (isOperator(String.valueOf(lastChar))) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                }
                currentInput += buttonTag;
            }
        } else {
            currentInput = currentInput.equals("0") ? buttonTag : currentInput + buttonTag;
        }
        solutionTv.setText(currentInput);
    }

    private boolean isOperator(String ch) {
        return "+-×÷%".contains(ch);
    }

    private void vibrateOnClick() {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50);
            }
        }
    }


    private String getResult(String expression) {
        try {
            double result = evaluateExpression(expression);
            return result == (long) result ? String.valueOf((long) result) : String.valueOf(result);
        } catch (Exception e) {
            return "Error";
        }
    }

    private double evaluateExpression(String expression) {
        double result = 0.0;
        char operator = '+';
        StringBuilder numberBuilder = new StringBuilder();

        for (char ch : expression.toCharArray()) {
            if (Character.isDigit(ch) || ch == '.') {
                numberBuilder.append(ch);
            } else {
                if (numberBuilder.length() > 0) {
                    result = applyOperation(result, Double.parseDouble(numberBuilder.toString()), operator);
                    numberBuilder.setLength(0);
                }
                operator = ch;
            }
        }

        if (numberBuilder.length() > 0) {
            result = applyOperation(result, Double.parseDouble(numberBuilder.toString()), operator);
        }

        return result;
    }

    private double applyOperation(double result, double number, char operator) {
        switch (operator) {
            case '+':
                return result + number;
            case '-':
                return result - number;
            case '×':
                return result * number;
            case '÷':
                if (number != 0) {
                    return result / number;
                } else {
                    throw new ArithmeticException("Division by zero");
                }
            case '%':
                return result % number;
            default:
                return result;
        }
    }
}
