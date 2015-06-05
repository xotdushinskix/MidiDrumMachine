package DM;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Main {

    JPanel mainPanel;  //компонент-контейнер для содержания пользовательского интерфейса
    ArrayList<JCheckBox> checkboxList; //коллекция для хранения флажков(меток)
    Sequencer sequencer;  //сенквенсор(устройство для записи и воспроизв-я музыки)
    Sequence sequence; //последовательность
    Track track;
    JFrame theFrame;

    String[] instrumentNames = {"Bass-Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Share", "Crash Cymbal",
            "Hand-Clap", "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap",
    "Low-mid Tom", "High Agogo", "Open Hi Conga"};  //массив,хранящий названия инструментов

    int[] instruments = {35, 42, 46, 38, 49 ,39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63}; //числа в массиве отвечают за канал для каждого инструмента

    public static void main(String[] args) {
        new Main().buildGui();
    }

    public void buildGui() {

        theFrame = new JFrame("Drum-machine"); //делаем фрейм
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        theFrame.setResizable(false);
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); //создаем пустую границу поля между краем панели и местои где будут распологаться компоненты


        checkboxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS); //расположение контейнера box по оси у

        JButton start = new JButton("Start"); //добавляем копку старт
        start.addActionListener(new MyStartListener()); //и метод MyStartListener прикрепляем к этой кнопке
        buttonBox.add(start); //запускаем

        JButton stop = new JButton("Stop"); //добавляем копку стоп
        stop.addActionListener(new MyStopListener());  //и метод MyStopListener прикрепляем к этой кнопке
        buttonBox.add(stop);  //запускаем

        JButton upTempo = new JButton("Tempo Up"); //добавляем копку стоп
        upTempo.addActionListener(new MyUpTempoListener());  //и метод MyUpTempoListener прикрепляем к этой кнопке
        buttonBox.add(upTempo);  //запускаем

        JButton downTempo = new JButton("Tempo Down"); //добавляем копку стоп
        downTempo.addActionListener(new MyDownTempoListener());  //и метод MyDownTempoListener прикрепляем к этой кнопке
        buttonBox.add(downTempo);  //запускаем

        Box nameBox = new Box(BoxLayout.Y_AXIS); //распологает кнопку в вертикальном положении
        for (int i = 0; i < 16; i++) {  //через все 16 инструментов
            nameBox.add(new Label(instrumentNames[i]));  //добавляем в каждой и-й элемент
        }

        background.add(BorderLayout.EAST, buttonBox);//распологаем кнопку на задний фон в левой части фрейма
        background.add(BorderLayout.WEST, nameBox); //распологаем контейнер box на задний фон в правой части фрейма

        theFrame.getContentPane().add(background); //добавляем содержимое панели на задний фон

        GridLayout grid = new GridLayout(16, 16); //делаем сетку 16 х 16
        grid.setVgap(1); //вертикально
        grid.setHgap(2);  //горизонтально
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel); //распологаем главную панель по-центру

        for (int i = 0; i < 256; i++) {  //по всей таблице 16х16
            JCheckBox c = new JCheckBox();
            c.setSelected(false);  //флажку присваиваем false, чтобы они не были остановлены
            checkboxList.add(c);  //добавляем в массив
            mainPanel.add(c);   //и на панель
        }

        setUpMidi();

        theFrame.setBounds(50, 50, 300, 300); //обозначаем границы фрейма
        theFrame.pack(); //формируем в одно целое
        theFrame.setVisible(true); //делаем видимым
    }

    public void setUpMidi() {  //миди метод для получения синтезатора, сенквенсора и дорожки
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
            sequence = new Sequence(Sequence.PPQ, 4); //создаем последовательность
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        track = sequence.createTrack(); //запускаем
        sequencer.setTempoInBPM(120);   //с тактом 220
    }

    public void buildTrackAndStart() {  //создаем массив на все 16 элементов,чтобы хранить значения
        int[] trackList = null;         //для каждого инструмента на все 16 тактов

        sequence.deleteTrack(track);  //избавляемся от старой дорожки
        track = sequence.createTrack();  //и добавляем новую

        for (int i = 0; i < 16; i++) {  //Это деляется для кажого из 16-ти рядов
            trackList = new int[16];

        int key = instruments[i]; //клавиша,которая содержит инструмент

        for (int j = 0; j < 16; j++) { //делаем это для каждого текущего ряда
            JCheckBox jc = (JCheckBox) checkboxList.get(j + (16*i));
            if(jc.isSelected()) {
                trackList[j] = key;  //если флажок установлен в этом такте,то помещаем знач-е клавиши в текущую ячейку массива
            } else {
                trackList[j] = 0; //если нет,то присваиваем ему 0
            }
        }

        makeTracks(trackList);  //для установленного инструмента и всех 16-ти тактов создаем событие и добавляем их на дорожку
        track.add(makeEvent(176, 1, 127, 0, 16)); //цифры,для установления определенного ритма
        }

        track.add(makeEvent(192, 9, 1, 0, 15));
        try {
            sequencer.setSequence(sequence);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY); //задаем непрерывный цикл повторения
        sequencer.start();
        sequencer.setTempoInBPM(120);
    }

    public class MyStartListener implements ActionListener { //первый из внутренних классов-слушатель для кнопок

        @Override
        public void actionPerformed(ActionEvent e) {
            buildTrackAndStart();
        }
    }

    public class MyStopListener implements ActionListener {  //второй внутренний класс-слушатель для кнопок

        @Override
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
        }
    }

    public class MyUpTempoListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));  //коєффициент темпа синтезатора по умолчанию = 1, поєтому щелчком міщи его можно изменить на +3 %
        }
    }

    public class MyDownTempoListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor * .97));  //коєффициент темпа синтезатора по умолчанию = 1, поєтому щелчком міщи его можно изменить на -3 %
        }
    }

    public void makeTracks(int[] list) { //метод создает события для одного инструмента за каждый проход цикла для все 16-ти тактов
        for (int i = 0; i < 16; i++) {
            int key = list[i];  //получаем int[] для нужного инструмента.каждый элемент массива будет содержать его клавишу,в противном случае будет содержать нуль

            if (key != 0) {
                track.add(makeEvent(144, 9, key, 100, i)); //создаем события вкл/выкл и добавляем их в дорожку
                track.add(makeEvent(128, 9, key, 100, i+1));
            }
        }
    }

    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) { //4 параметра для сообщения, событие tick происходит в момент появления данного сообщения

        MidiEvent event = null;
        ShortMessage a = new ShortMessage();     //cоздаем сообщение и событие
        try {
            a.setMessage(comd, chan, one, two);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        event = new MidiEvent(a, tick);
                                                   //используя параметры метода
        return event;
    }

}
