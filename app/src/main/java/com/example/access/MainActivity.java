package com.example.access;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView resultTv, solutionTv;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        resultTv = findViewById(R.id.result_tv);
        solutionTv = findViewById(R.id.solution_tv);

        assignId(R.id.btn_c);
        assignId(R.id.btn_del);
        assignId(R.id.btn_percent);
        assignId(R.id.btn_divide);
        assignId(R.id.btn_7);
        assignId(R.id.btn_8);
        assignId(R.id.btn_9);
        assignId(R.id.btn_multiply);
        assignId(R.id.btn_4);
        assignId(R.id.btn_5);
        assignId(R.id.btn_6);
        assignId(R.id.btn_add);
        assignId(R.id.btn_1);
        assignId(R.id.btn_2);
        assignId(R.id.btn_3);
        assignId(R.id.btn_subtract);
        assignId(R.id.btn_point);
        assignId(R.id.btn_0);
        assignId(R.id.btn_equals);
    }

    void assignId(int id) {
        MaterialButton btn = findViewById(id);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        MaterialButton button = (MaterialButton) view;
        String buttonText = button.getText().toString();
        String dataToCalculate = solutionTv.getText().toString();

        vibrateOnClick();

        switch (buttonText) {
            case "C":
                solutionTv.setText("");
                resultTv.setText("0");
                return;
            case "⌫":
                if (!dataToCalculate.isEmpty()) {
                    dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1);
                }
                break;
            case "=":
                String result = getResult(dataToCalculate);
                resultTv.setText(result);
                return;
            default:
                if (isOperator(buttonText)) {
                    if (dataToCalculate.isEmpty()) {
                        if (buttonText.equals("-")) {
                            dataToCalculate = "-";
                        }
                    } else {
                        char lastChar = dataToCalculate.charAt(dataToCalculate.length() - 1);

                        if (isOperator(Character.toString(lastChar))) {
                            if (buttonText.equals("-") && lastChar != '-') {
                                dataToCalculate += buttonText;
                            } else {
                                dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1) + buttonText;
                            }
                        } else {
                            dataToCalculate += buttonText;
                        }
                    }
                } else {
                    if (dataToCalculate.equals("0")) {
                        dataToCalculate = buttonText;
                    } else {
                        dataToCalculate += buttonText;
                    }
                }
                break;
        }

        solutionTv.setText(dataToCalculate);
    }

    private boolean isOperator(String ch) {
        return ch.equals("+") || ch.equals("-") || ch.equals("×") || ch.equals("÷") || ch.equals("%");
    }

    private void vibrateOnClick() {
        if (vibrator != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50);
            }
        }
    }

    String getResult(String data) {
        try {
            double result = evaluateExpression(data);
            if (result == (long) result) {
                return String.valueOf((long) result);
            } else {
                return String.valueOf(result);
            }
        } catch (Exception e) {
            return "Error";
        }
    }

    double evaluateExpression(String data) {
        double result = 0.0;
        char lastOperator = '+';
        StringBuilder number = new StringBuilder();

        for (int i = 0; i < data.length(); i++) {
            char ch = data.charAt(i);

            if (ch == '-' && (i == 0 || isOperator(Character.toString(data.charAt(i - 1))))) {
                number.append(ch);
            } else if (Character.isDigit(ch) || ch == '.') {
                number.append(ch);
            } else {
                if (number.length() > 0) {
                    result = applyOperation(result, Double.parseDouble(number.toString()), lastOperator);
                    number.setLength(0);
                }
                lastOperator = ch;
            }
        }

        if (number.length() > 0) {
            result = applyOperation(result, Double.parseDouble(number.toString()), lastOperator);
        }

        return result;
    }

    double applyOperation(double result, double number, char operator) {
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
