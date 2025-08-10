import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class NumberGuessingGame extends JFrame{
    private int numberToGuess;
    private  int numberOfTries;
    private JTextField guessField;
    private JLabel messageLabel;

    public NumberGuessingGame(){
        setTitle("Number Guessing Game");

        setSize(400,200);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new FlowLayout());

        JLabel titleLabel = new JLabel("Guess the number between 1 and 100");
        titleLabel.setFont(new Font("Arial", Font.BOLD,16));
        add(titleLabel);

        guessField = new JTextField(10);
        add(guessField);

        JButton guessButton = new JButton("Guess");
        add(guessButton);

        messageLabel = new JLabel("Enter your guess and press Guess. ");
        messageLabel.setFont(new Font("Arial", Font.PLAIN,14));
        add(messageLabel);

        startnewgame();

        guessButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleGuess();
            }
        });
        setLocationRelativeTo(null);

        setVisible(true);

        }

        private void startnewgame(){
        Random random = new Random();
        numberToGuess = random.nextInt(100)+1;
        numberOfTries = 0;
        messageLabel.setText("New game started! Enter your guess. ");
        guessField.setText("");
        }

        private void handleGuess(){
        try{
            int guess = Integer.parseInt(guessField.getText());
            numberOfTries++;

            if(guess < 1 || guess > 100){
                messageLabel.setText("Guess between 1 and 100");
            }else if (guess < numberToGuess){
                messageLabel.setText("Too Low! Try Again.");
            }else if(guess > numberToGuess){
                messageLabel.setText("Too High! Try Again.");
            }else{
                JOptionPane.showMessageDialog(this, "Congratulations! You Guessed it in "+ numberOfTries + "tries.","Winner!",JOptionPane.INFORMATION_MESSAGE);

                int playAgain = JOptionPane.showConfirmDialog(this, "Play again?","Number Guessing Game",JOptionPane.YES_NO_OPTION);

                if(playAgain == JOptionPane.YES_OPTION){
                    startnewgame();
                }else{
                    System.exit(0);
                }
            }
            guessField.setText("");
        } catch(NumberFormatException ex){
            messageLabel.setText("Please enter a valid number.");
        }

        }
        public static void main(String[] args){
            NumberGuessingGame numberGuessingGame = new NumberGuessingGame();
        }


    }


