import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class OnlineExam extends JFrame {
    static class User {
        String username;
        String password;
        String fullName;
        String email;
        public User(String u, String p, String name, String email) {
            this.username = u;
            this.password = p;
            this.fullName = name;
            this.email = email;
        }
    }

    /** Question data structure */
    static class Question {
        String text;
        String[] options;
        int correctIndex; // correct answer index
        public Question(String text, String[] options, int correctIndex) {
            this.text = text;
            this.options = options;
            this.correctIndex = correctIndex;
        }
    }


    Map<String, User> users = new HashMap<>();

    java.util.List<Question> questions = new ArrayList<>();

    CardLayout cards = new CardLayout();
    JPanel cardPanel = new JPanel(cards);

    // Login
    JTextField loginUser = new JTextField();
    JPasswordField loginPass = new JPasswordField();

    // Profile
    JLabel profUserLabel = new JLabel();
    JTextField profNameField = new JTextField();
    JTextField profEmailField = new JTextField();
    JPasswordField profOldPass = new JPasswordField();
    JPasswordField profNewPass = new JPasswordField();

    // Exam
    JLabel timerLabel = new JLabel("Time: --:--");
    JLabel qLabel = new JLabel();
    JRadioButton[] optionButtons = new JRadioButton[4];
    ButtonGroup optGroup = new ButtonGroup();
    JButton prevBtn = new JButton("Previous");
    JButton nextBtn = new JButton("Next");
    JButton submitBtn = new JButton("Submit");

    // State variables
    User currentUser = null;
    int currentIndex = 0;
    int[] answers; // -1 means not answered

    javax.swing.Timer swingTimer;
    int remainingSeconds = 0;

    // Constructor
    public OnlineExam() {
        super("Online Examination - Demo");
        loadQuestions(); // Load demo questions
        initUI();
        setSize(800, 500);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // Confirm before closing
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });
    }

    // Build UI
    void initUI() {
        cardPanel.add(buildLoginPanel(), "login");
        cardPanel.add(buildProfilePanel(), "profile");
        cardPanel.add(buildExamPanel(), "exam");
        cardPanel.add(buildResultPanel(), "result");
        add(cardPanel);
        showLogin();
    }

    JPanel buildLoginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Online Exam System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        c.gridx=0; c.gridy=0; c.gridwidth=2;
        p.add(title, c);

        c.gridwidth=1;
        c.gridy++;
        p.add(new JLabel("Username:"), c);
        c.gridx=1; p.add(loginUser, c);

        c.gridx=0; c.gridy++;
        p.add(new JLabel("Password:"), c);
        c.gridx=1; p.add(loginPass, c);

        c.gridx=0; c.gridy++; c.gridwidth=2;
        JButton loginBtn = new JButton("Login");
        p.add(loginBtn, c);

        c.gridy++;
        JButton regBtn = new JButton("Register (New User)");
        p.add(regBtn, c);

        // Actions
        loginBtn.addActionListener(e -> doLogin());
        regBtn.addActionListener(e -> doRegister());

        return p;
    }

    void doLogin() {
        String u = loginUser.getText().trim();
        String p = new String(loginPass.getPassword());
        if(u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password");
            return;
        }
        User user = users.get(u);
        if(user==null || !user.password.equals(p)) {
            JOptionPane.showMessageDialog(this, "Invalid username/password");
            return;
        }
        currentUser = user;
        showProfile();
        loginPass.setText("");
    }

    void doRegister() {
        String u = loginUser.getText().trim();
        String p = new String(loginPass.getPassword());
        if(u.isEmpty()||p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter desired username & password in login fields then click Register");
            return;
        }
        if(users.containsKey(u)) {
            JOptionPane.showMessageDialog(this, "Username already exists");
            return;
        }
        String name = JOptionPane.showInputDialog(this, "Full name:");
        if(name==null) return;
        String email = JOptionPane.showInputDialog(this, "Email (optional):");
        User nu = new User(u,p,name, email==null?"":email);
        users.put(u, nu);
        JOptionPane.showMessageDialog(this, "Registered. You can now login.");
    }

    JPanel buildProfilePanel() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logout = new JButton("Logout");
        JButton startExam = new JButton("Start Exam");
        top.add(startExam);
        top.add(logout);
        p.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Show profile fields
        c.gridx=0; c.gridy=0; center.add(new JLabel("Username:"), c);
        c.gridx=1; center.add(profUserLabel, c);

        c.gridx=0; c.gridy++; center.add(new JLabel("Full name:"), c);
        c.gridx=1; center.add(profNameField, c);

        c.gridx=0; c.gridy++; center.add(new JLabel("Email:"), c);
        c.gridx=1; center.add(profEmailField, c);

        c.gridx=0; c.gridy++; center.add(new JLabel("Old Password:"), c);
        c.gridx=1; center.add(profOldPass, c);

        c.gridx=0; c.gridy++; center.add(new JLabel("New Password:"), c);
        c.gridx=1; center.add(profNewPass, c);

        JButton saveProfile = new JButton("Update Profile / Change Password");
        c.gridx=0; c.gridy++; c.gridwidth=2; center.add(saveProfile, c);

        p.add(center, BorderLayout.CENTER);

        // Actions
        logout.addActionListener(e -> doLogout());
        startExam.addActionListener(e -> startExamForCurrentUser());
        saveProfile.addActionListener(e -> saveProfile());

        return p;
    }

    void showProfile() {
        if(currentUser==null) { showLogin(); return; }
        profUserLabel.setText(currentUser.username);
        profNameField.setText(currentUser.fullName);
        profEmailField.setText(currentUser.email);
        profOldPass.setText("");
        profNewPass.setText("");
        cards.show(cardPanel, "profile");
    }

    void saveProfile() {
        if(currentUser==null) return;
        currentUser.fullName = profNameField.getText().trim();
        currentUser.email = profEmailField.getText().trim();

        String oldp = new String(profOldPass.getPassword());
        String newp = new String(profNewPass.getPassword());

        if(!oldp.isEmpty()||!newp.isEmpty()) {
            if(!currentUser.password.equals(oldp)) {
                JOptionPane.showMessageDialog(this, "Old password incorrect");
                return;
            }
            if(newp.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter new password");
                return;
            }
            currentUser.password = newp;
        }
        JOptionPane.showMessageDialog(this, "Profile updated");
    }

    void doLogout() {
        stopTimerIfRunning();
        currentUser = null;
        answers = null;
        currentIndex = 0;
        showLogin();
    }

    JPanel buildExamPanel() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(timerLabel);
        JButton endSession = new JButton("Close Session");
        top.add(endSession);
        p.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        qLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        center.add(qLabel, BorderLayout.NORTH);

        JPanel opts = new JPanel(new GridLayout(4,1,6,6));
        for(int i=0;i<4;i++){
            optionButtons[i] = new JRadioButton();
            optGroup.add(optionButtons[i]);
            final int idx = i;
            optionButtons[i].addActionListener(ae -> saveAnswerForCurrent(idx));
            opts.add(optionButtons[i]);
        }
        center.add(opts, BorderLayout.CENTER);
        p.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(prevBtn);
        bottom.add(nextBtn);
        bottom.add(submitBtn);
        p.add(bottom, BorderLayout.SOUTH);

        // Actions
        prevBtn.addActionListener(e -> goPrevious());
        nextBtn.addActionListener(e -> goNext());
        submitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Submit exam now?", "Confirm", JOptionPane.YES_NO_OPTION);
            if(confirm==JOptionPane.YES_OPTION) submitExam();
        });

        endSession.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Close session and logout?", "Close Session", JOptionPane.YES_NO_OPTION);
            if(c==JOptionPane.YES_OPTION) doLogout();
        });

        return p;
    }

    void startExamForCurrentUser() {
        if(currentUser==null) {
            JOptionPane.showMessageDialog(this, "No user logged in");
            return;
        }
        answers = new int[questions.size()];
        Arrays.fill(answers, -1);
        currentIndex = 0;

        // Timer set for 2 minutes demo (120 sec)
        remainingSeconds = 2*60;
        startTimer();

        showQuestion(0);
        cards.show(cardPanel, "exam");
    }

    void showQuestion(int idx) {
        if(idx<0||idx>=questions.size()) return;
        currentIndex = idx;
        Question q = questions.get(idx);
        qLabel.setText((idx+1)+". "+q.text);

        for(int i=0;i<4;i++){
            optionButtons[i].setText((i+1)+") "+q.options[i]);
        }

        optGroup.clearSelection();
        if(answers[idx]>=0 && answers[idx]<4) optionButtons[answers[idx]].setSelected(true);

        prevBtn.setEnabled(idx>0);
        nextBtn.setEnabled(idx<questions.size()-1);
    }

    void saveAnswerForCurrent(int optIdx) {
        answers[currentIndex] = optIdx;
    }

    void goNext() { saveCurrentSelectionFromUI(); if(currentIndex<questions.size()-1) showQuestion(currentIndex+1); }
    void goPrevious() { saveCurrentSelectionFromUI(); if(currentIndex>0) showQuestion(currentIndex-1); }

    void saveCurrentSelectionFromUI() {
        for(int i=0;i<4;i++) if(optionButtons[i].isSelected()) answers[currentIndex]=i;
    }

    void submitExam() {
        stopTimerIfRunning();
        saveCurrentSelectionFromUI();
        int correct = 0;
        int attempted = 0;

        for(int i=0;i<questions.size();i++){
            if(answers[i]>=0) {
                attempted++;
                if(answers[i]==questions.get(i).correctIndex) correct++;
            }
        }
        String res = String.format("User: %s\nScore: %d / %d\nAttempted: %d\n", currentUser.username, correct, questions.size(), attempted);
        JOptionPane.showMessageDialog(this, res);
        cards.show(cardPanel, "result");
    }

    JPanel buildResultPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        p.add(new JScrollPane(ta), BorderLayout.CENTER);
        JButton back = new JButton("Back to Profile");
        back.addActionListener(e -> showProfile());
        p.add(back, BorderLayout.SOUTH);
        return p;
    }

    void startTimer() {
        updateTimerLabel();
        swingTimer = new javax.swing.Timer(1000, e -> {
            remainingSeconds--;
            if(remainingSeconds<=0) {
                swingTimer.stop();
                JOptionPane.showMessageDialog(this, "Time's up! Exam will be auto-submitted.");
                submitExam();
            } else updateTimerLabel();
        });
        swingTimer.start();
    }

    void stopTimerIfRunning(){
        if(swingTimer!=null && swingTimer.isRunning()) swingTimer.stop();
    }

    void updateTimerLabel() {
        int mins = remainingSeconds/60;
        int secs = remainingSeconds%60;
        timerLabel.setText(String.format("Time: %02d:%02d", mins, secs));
    }

    void showLogin() { cards.show(cardPanel, "login"); }

    // -------------------- EXIT --------------------
    void onExit() {
        int c = JOptionPane.showConfirmDialog(this, "Exit application? You'll be logged out.", "Exit", JOptionPane.YES_NO_OPTION);
        if(c==JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    // -------------------- QUESTIONS --------------------
    void loadQuestions() {
        // Demo hard-coded questions
        questions.add(new Question("Which language runs in a web browser?", new String[]{"Java","C","Python","JavaScript"}, 3));
        questions.add(new Question("What does JVM stand for?", new String[]{"Java Variable Machine","Java Virtual Machine","Just Virtual Machine","Java Visual Machine"}, 1));
        questions.add(new Question("Which collection class allows duplicates?", new String[]{"Set","List","Map","None"}, 1));
    }

    // -------------------- MAIN --------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new OnlineExam().setVisible(true);
        });
    }
}

