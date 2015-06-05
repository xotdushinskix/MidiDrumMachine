package DM;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Main {

    JPanel mainPanel;  //���������-��������� ��� ���������� ����������������� ����������
    ArrayList<JCheckBox> checkboxList; //��������� ��� �������� �������(�����)
    Sequencer sequencer;  //����������(���������� ��� ������ � ���������-� ������)
    Sequence sequence; //������������������
    Track track;
    JFrame theFrame;

    String[] instrumentNames = {"Bass-Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Share", "Crash Cymbal",
            "Hand-Clap", "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap",
    "Low-mid Tom", "High Agogo", "Open Hi Conga"};  //������,�������� �������� ������������

    int[] instruments = {35, 42, 46, 38, 49 ,39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63}; //����� � ������� �������� �� ����� ��� ������� �����������

    public static void main(String[] args) {
        new Main().buildGui();
    }

    public void buildGui() {

        theFrame = new JFrame("Drum-machine"); //������ �����
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        theFrame.setResizable(false);
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); //������� ������ ������� ���� ����� ����� ������ � ������ ��� ����� ������������� ����������


        checkboxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS); //������������ ���������� box �� ��� �

        JButton start = new JButton("Start"); //��������� ����� �����
        start.addActionListener(new MyStartListener()); //� ����� MyStartListener ����������� � ���� ������
        buttonBox.add(start); //���������

        JButton stop = new JButton("Stop"); //��������� ����� ����
        stop.addActionListener(new MyStopListener());  //� ����� MyStopListener ����������� � ���� ������
        buttonBox.add(stop);  //���������

        JButton upTempo = new JButton("Tempo Up"); //��������� ����� ����
        upTempo.addActionListener(new MyUpTempoListener());  //� ����� MyUpTempoListener ����������� � ���� ������
        buttonBox.add(upTempo);  //���������

        JButton downTempo = new JButton("Tempo Down"); //��������� ����� ����
        downTempo.addActionListener(new MyDownTempoListener());  //� ����� MyDownTempoListener ����������� � ���� ������
        buttonBox.add(downTempo);  //���������

        Box nameBox = new Box(BoxLayout.Y_AXIS); //����������� ������ � ������������ ���������
        for (int i = 0; i < 16; i++) {  //����� ��� 16 ������������
            nameBox.add(new Label(instrumentNames[i]));  //��������� � ������ �-� �������
        }

        background.add(BorderLayout.EAST, buttonBox);//����������� ������ �� ������ ��� � ����� ����� ������
        background.add(BorderLayout.WEST, nameBox); //����������� ��������� box �� ������ ��� � ������ ����� ������

        theFrame.getContentPane().add(background); //��������� ���������� ������ �� ������ ���

        GridLayout grid = new GridLayout(16, 16); //������ ����� 16 � 16
        grid.setVgap(1); //�����������
        grid.setHgap(2);  //�������������
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel); //����������� ������� ������ ��-������

        for (int i = 0; i < 256; i++) {  //�� ���� ������� 16�16
            JCheckBox c = new JCheckBox();
            c.setSelected(false);  //������ ����������� false, ����� ��� �� ���� �����������
            checkboxList.add(c);  //��������� � ������
            mainPanel.add(c);   //� �� ������
        }

        setUpMidi();

        theFrame.setBounds(50, 50, 300, 300); //���������� ������� ������
        theFrame.pack(); //��������� � ���� �����
        theFrame.setVisible(true); //������ �������
    }

    public void setUpMidi() {  //���� ����� ��� ��������� �����������, ����������� � �������
        try {
            sequencer = MidiSystem.getSequencer();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
        try {
            sequencer.open();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
        try {
            sequence = new Sequence(Sequence.PPQ, 4); //������� ������������������
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        track = sequence.createTrack(); //���������
        sequencer.setTempoInBPM(120);   //� ������ 220
    }

    public void buildTrackAndStart() {  //������� ������ �� ��� 16 ���������,����� ������� ��������
        int[] trackList = null;         //��� ������� ����������� �� ��� 16 ������

        sequence.deleteTrack(track);  //����������� �� ������ �������
        track = sequence.createTrack();  //� ��������� �����

        for (int i = 0; i < 16; i++) {  //��� �������� ��� ������ �� 16-�� �����
            trackList = new int[16];

        int key = instruments[i]; //�������,������� �������� ����������

        for (int j = 0; j < 16; j++) { //������ ��� ��� ������� �������� ����
            JCheckBox jc = (JCheckBox) checkboxList.get(j + (16*i));
            if(jc.isSelected()) {
                trackList[j] = key;  //���� ������ ���������� � ���� �����,�� �������� ����-� ������� � ������� ������ �������
            } else {
                trackList[j] = 0; //���� ���,�� ����������� ��� 0
            }
        }

        makeTracks(trackList);  //��� �������������� ����������� � ���� 16-�� ������ ������� ������� � ��������� �� �� �������
        track.add(makeEvent(176, 1, 127, 0, 16)); //�����,��� ������������ ������������� �����
        }

        track.add(makeEvent(192, 9, 1, 0, 15));
        try {
            sequencer.setSequence(sequence);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY); //������ ����������� ���� ����������
        sequencer.start();
        sequencer.setTempoInBPM(120);
    }

    public class MyStartListener implements ActionListener { //������ �� ���������� �������-��������� ��� ������

        @Override
        public void actionPerformed(ActionEvent e) {
            buildTrackAndStart();
        }
    }

    public class MyStopListener implements ActionListener {  //������ ���������� �����-��������� ��� ������

        @Override
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
        }
    }

    public class MyUpTempoListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));  //���������� ����� ����������� �� ��������� = 1, ������ ������� ��� ��� ����� �������� �� +3 %
        }
    }

    public class MyDownTempoListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor * .97));  //���������� ����� ����������� �� ��������� = 1, ������ ������� ��� ��� ����� �������� �� -3 %
        }
    }

    public void makeTracks(int[] list) { //����� ������� ������� ��� ������ ����������� �� ������ ������ ����� ��� ��� 16-�� ������
        for (int i = 0; i < 16; i++) {
            int key = list[i];  //�������� int[] ��� ������� �����������.������ ������� ������� ����� ��������� ��� �������,� ��������� ������ ����� ��������� ����

            if (key != 0) {
                track.add(makeEvent(144, 9, key, 100, i)); //������� ������� ���/���� � ��������� �� � �������
                track.add(makeEvent(128, 9, key, 100, i+1));
            }
        }
    }

    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) { //4 ��������� ��� ���������, ������� tick ���������� � ������ ��������� ������� ���������

        MidiEvent event = null;
        ShortMessage a = new ShortMessage();     //c������ ��������� � �������
        try {
            a.setMessage(comd, chan, one, two);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        event = new MidiEvent(a, tick);
                                                   //��������� ��������� ������
        return event;
    }

}
