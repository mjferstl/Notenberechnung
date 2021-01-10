package gui;

import excel.ExcelWorkbookCreator;
import extras.Error;
import log.Log;
import log.LogType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import school.*;
import school.exercise.Exercise;
import school.exercise.NormalExercise;
import school.exercise.TextproductionExercise;
import utils.UpdatePublisher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainGUI implements UpdatePublisher, IF_GUI {

    // shell
    protected Shell shell;

    public final static int BACKGROUND_COLOR_RGB_RED = 245;
    public final static int BACKGROUND_COLOR_RGB_GREEN = 245;
    public final static int BACKGROUND_COLOR_RGB_BLUE = 245;

    // strings
    private final String ARROW_UPWARDS = "\u2191";
    private final String ARROW_DOWNWARDS = "\u2193";

    private final String[] titles = {"", "Bezeichnung", "Bewertung"};

    // buttons
    private Button btnRemoveTask;
    private Button btnMoveUp;
    private Button btnMoveDown;
    private OpenFileButton btnOpenExcel;

    // custom objects
    private SchoolClass schoolClass;

    // tables
    private static Table tabExercises;

    private final Log log;

    public MainGUI(Log log) {
        this.log = log;
    }

    public void open() {
        Display display = Display.getDefault();
        createContents();
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {

        final MainGUI gui = this;

        shell = new Shell();

        // Set the icon and text in the title bar
        Image icon = new Image(shell.getDisplay(), "src/gui/icon.png");
        shell.setImage(icon);
        shell.setText("Erstellen einer Excel-Datei zur Notenauswertung");

        // set the size and layout
        shell.setSize(600, 400);
        shell.setLayout(new GridLayout(3, false));
        shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 5));
        shell.setBackground(new Color(shell.getDisplay(), BACKGROUND_COLOR_RGB_RED, BACKGROUND_COLOR_RGB_GREEN,
                BACKGROUND_COLOR_RGB_BLUE, 0));

        Color transparentBackgroundColor = new Color(shell.getDisplay(), 255, 255, 255, 0);

        Label lblSchoolClassList = new Label(shell, SWT.NONE);
        lblSchoolClassList.setText("Klassenliste");
        lblSchoolClassList.setBackground(transparentBackgroundColor);
        lblSchoolClassList.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        // Create a label with borders
        Text lblSchoolClassFile = new Text(shell, SWT.READ_ONLY | SWT.BORDER);
        lblSchoolClassFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        // White background
        lblSchoolClassFile.setBackground(new Color(shell.getDisplay(), 255, 255, 255));

        Button btnBrowse = new Button(shell, SWT.NONE);
        btnBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fd = new FileDialog(shell, SWT.OPEN);
                fd.setText("Klassenliste auswählen...");
                String[] filterExt = {"*.txt", "*.*"};
                fd.setFilterExtensions(filterExt);
                String selected = fd.open();

                if (selected == null) {
                    addLogMessage("Keine Datei ausgewählt", LogType.ERROR);
                    lblSchoolClassFile.setText("Keine Datei ausgewählt");
                    lblSchoolClassFile.requestLayout();
                } else {

                    String fileName = fd.getFileName();
                    String fileDirectory = fd.getFilterPath();

                    lblSchoolClassFile.setText(String.format(" %s\\%s", fd.getFilterPath(), fd.getFileName()));
                    lblSchoolClassFile.requestLayout();

                    addLogMessage(String.format("Klassenliste \"%s\" ausgewählt", fileName), LogType.INFO);

                    schoolClass = new SchoolClass();
                    String filePath = fileDirectory + "\\" + fileName;
                    File selectedFile = new File(filePath);
                    Error error = schoolClass.loadStudentsFromFile(selectedFile);

                    // update logwindow
                    if (error.getErrorLevel() == 0) {
                        addLogMessage(error.getMessage(), LogType.INFO);
                    } else {
                        addLogMessage(error.getMessage(), LogType.ERROR);
                    }
                }
            }
        });
        btnBrowse.setText("Durchsuchen...");

        Label lblExercises = new Label(shell, SWT.NONE);
        lblExercises.setText("Aufgaben");
        lblExercises.setBackground(transparentBackgroundColor);

        tabExercises = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
        tabExercises.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                if ((tabExercises.getItemCount() > 0)) {
                    int selectedIndex = tabExercises.getSelectionIndex();
                    TableItem ti = tabExercises.getItem(selectedIndex);
                    Exercise exercise;
                    if (ti.getText(0).equals(NormalExercise.SHORT_KEY)) {
                        exercise = NormalExercise.parseTextToAufgabe(ti.getText(1), ti.getText(2));
                    } else if (ti.getText(0).equals(TextproductionExercise.SHORT_KEY)) {
                        exercise = TextproductionExercise.parseTextToTextproduktion(ti.getText(1), ti.getText(2));
                    } else {
                        return;
                    }

                    ExerciseDialog exerciseDialog = new ExerciseDialog(gui, shell, exercise, selectedIndex);
                    exerciseDialog.open();
                }
            }
        });
        GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3);
        gd_table.heightHint = 25;
        tabExercises.setLayoutData(gd_table);
        tabExercises.setHeaderVisible(true);
        tabExercises.setLinesVisible(true);

        for (String title : titles) {
            TableColumn column = new TableColumn(tabExercises, SWT.NONE);
            column.setText(title);
        }

        for (int i = 0; i < titles.length; i++) {
            tabExercises.getColumn(i).pack();
        }

        Group group = new Group(shell, SWT.NONE);
        group.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
        btnMoveUp = new Button(group, SWT.NONE);
        btnMoveUp.setBounds(3, 47, 50, 25);
        btnMoveUp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if ((tabExercises.getItemCount() != 0) && (tabExercises.getSelection() != null)
                        && (tabExercises.getSelectionIndex() != 0)) {
                    int selectedItem = tabExercises.getSelectionIndex();
                    moveExercise(selectedItem, "upwards");
                }
            }
        });
        btnMoveUp.setText(ARROW_UPWARDS);

        Button btnAddExercise = new Button(group, SWT.NONE);
        btnAddExercise.setBounds(3, 10, 50, 25);
        btnAddExercise.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openNewExerciseDialog(gui);
            }
        });
        btnAddExercise.setText("+");

        btnRemoveTask = new Button(group, SWT.NONE);
        btnRemoveTask.setBounds(56, 10, 50, 25);
        btnRemoveTask.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeTaskFromTable(tabExercises);
            }
        });
        btnRemoveTask.setText("-");

        btnMoveDown = new Button(group, SWT.NONE);
        btnMoveDown.setBounds(3, 78, 50, 25);
        btnMoveDown.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if ((tabExercises.getItemCount() != 0) && (tabExercises.getSelection() != null)
                        && (tabExercises.getSelectionIndex() != tabExercises.getItemCount())) {
                    int selectedItem = tabExercises.getSelectionIndex();
                    moveExercise(selectedItem, "downwards");
                }
            }
        });
        btnMoveDown.setText(ARROW_DOWNWARDS);

        new Label(shell, SWT.NONE);
        new Label(shell, SWT.NONE);

        Button btnCreateExcel = new Button(shell, SWT.NONE);
        btnCreateExcel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1));
        btnCreateExcel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createExcelFile();
            }
        });
        btnCreateExcel.setText("Excel erstellen");

        Label space = new Label(shell, SWT.FILL);
        space.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        space.setBackground(transparentBackgroundColor);

        btnOpenExcel = new OpenFileButton(this, shell, SWT.NONE);
        btnOpenExcel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1));
        btnOpenExcel.setText("Excel öffnen");
        btnOpenExcel.deactivate();

        log.createSwtLog(shell);
        log.getSwtLog().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
        log.getSwtLog().setBackground(transparentBackgroundColor);

        Label versionInfoLabel = new Label(shell, SWT.NONE);
        versionInfoLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 3, 1));
        versionInfoLabel.setText("v" + Main.VERSION);
        versionInfoLabel.setBackground(transparentBackgroundColor);

        // set all buttons enabled/disabled depending on the current contents
        setButtonsEnables();
    }

    /**
     * Method to start creating the excel worksheet based on the school class file,
     * which the user selected, and the tasks, which have been created
     *
     * @author Mathias Ferstl
     */
    private void createExcelFile() {

        btnOpenExcel.deactivate();

        addLogMessage("Excel-Datei wird erstellt...", LogType.INFO);
        List<Exercise> exercises = parseExercisesFromGUI();
        ExcelWorkbookCreator creator = new ExcelWorkbookCreator(this, schoolClass, exercises);
        File excelFile = creator.createXlsxFile();

        if (excelFile != null && excelFile.exists()) {
            btnOpenExcel.activate(excelFile);
        } else {
            btnOpenExcel.deactivate();
        }
    }

    private void openNewExerciseDialog(IF_GUI par) {
        // open a new dialog for creating a task
        ExerciseDialog na = new ExerciseDialog(par, shell);
        na.open();

        // update the buttons
        setButtonsEnables();
    }

    /**
     * Set the buttons of the GUI enabled/disabled, depending on the created tasks
     * etc.
     *
     * @author Mathias Ferstl
     */
    private void setButtonsEnables() {

        // buttons for moving items up and down
        if (tabExercises.getItemCount() >= 2) {
            btnMoveUp.setEnabled(true);
            btnMoveDown.setEnabled(true);
        } else {
            btnMoveUp.setEnabled(false);
            btnMoveDown.setEnabled(false);
        }

        btnRemoveTask.setEnabled(tabExercises.getItemCount() != 0);
    }

    public void addLogMessage(String message) {
        log.addMessage(message);
    }

    public void addLogMessage(String message, LogType logType) {
        log.addMessage(message, logType);
    }

    /**
     * Method to parse the created exercises from the GUI table
     *
     * @return list of objects implementing the ExerciseInterface
     * @author Mathias Ferstl
     */
    private List<Exercise> parseExercisesFromGUI() {

        List<Exercise> exerciseList = new ArrayList<>();

        for (int i = 0; i < tabExercises.getItemCount(); i++) {
            TableItem ti = tabExercises.getItem(i);
            Exercise exercise = parseTableItemToExercise(ti);
            exerciseList.add(exercise);
        }

        return exerciseList;
    }

    private Exercise parseTableItemToExercise(TableItem tableItem) {

        String bezeichnung = tableItem.getText(1);
        String text = tableItem.getText(2);

        return switch (tableItem.getText(0)) {
            case NormalExercise.SHORT_KEY -> NormalExercise.parseTextToAufgabe(bezeichnung, text);
            case TextproductionExercise.SHORT_KEY -> TextproductionExercise.parseTextToTextproduktion(bezeichnung, text);
            default -> throw new IllegalStateException("Unexpected value: " + tableItem.getText(0));
        };
    }

    /**
     * Method to add a task to the table
     *
     * @param task: object of interface ExerciseInterface
     * @author Mathias Ferstl
     */
    @Override
    public void addTask(Exercise task) {
        TableItem item = new TableItem(tabExercises, SWT.NONE);
        item.setText(0, task.getKey());
        item.setText(1, task.getName());
        item.setText(2, task.getConfigString());
        fitTableColumnsWidth(tabExercises);

        btnOpenExcel.deactivate();

        addLogMessage(String.format("Aufgabe \"%s\" erstellt.", task.getName()));
    }

    /**
     * Method to update a task, which is already in the GUI's table
     *
     * @param exercise Exercise
     * @param tableIndex: index of the table item, which should be updated
     * @author Mathias Ferstl
     */
    @Override
    public void updateTask(Exercise exercise, int tableIndex) {
        TableItem item = tabExercises.getItem(tableIndex);
        item.setText(0, exercise.getKey());
        item.setText(1, exercise.getName());
        item.setText(2, exercise.getConfigString());
        fitTableColumnsWidth(tabExercises);

        btnOpenExcel.deactivate();

        addLogMessage(String.format("Aufgabe \"%s\" aktualisiert", exercise.getName()), LogType.INFO);
    }

    /**
     * Remove a task from the table. The item to remove is the currently selected
     * item
     *
     * @param table Table, which contains the item, that should be removed
     * @author Mathias Ferstl
     */
    private void removeTaskFromTable(Table table) {
        if ((table.getItemCount() != 0) && (table.getSelectionIndex() >= 0)) {
            int index = table.getSelectionIndex();
            TableItem item = table.getItem(index);
            Exercise exercise = parseTableItemToExercise(item);

            table.remove(index);

            addLogMessage(String.format("Aufgabe \"%s\" gelöscht", exercise.getName()), LogType.INFO);

            // update the buttons
            setButtonsEnables();
        }

        btnOpenExcel.deactivate();
    }

    /**
     * Method to set the width of all columns in the table to it's longest content
     *
     * @author Mathias Ferstl
     */
    private void fitTableColumnsWidth(Table t) {
        for (int i = 0, n = t.getColumnCount(); i < n; i++) {
            t.getColumn(i).pack();
        }
    }

    /**
     * Method to move table items up- or downwards in the GUI's table
     *
     * @param index:     number representing the index of the table item in the
     *                   table
     * @param direction: String containing "upwards" or "downwards"
     * @author Mathias Ferstl
     */
    private void moveExercise(int index, String direction) {
        if (index >= 0 && (direction.equals("upwards") || direction.equals("downwards"))) {
            TableItem tableItem = tabExercises.getItem(index);
            String[] tableItemContent = {tableItem.getText(0), tableItem.getText(1), tableItem.getText(2)};
            tableItem.dispose();

            TableItem newTableItem;
            if (direction.equals("upwards") && index > 0) {
                newTableItem = new TableItem(tabExercises, SWT.NONE, index - 1);
            } else if (direction.equals("downwards")) {
                newTableItem = new TableItem(tabExercises, SWT.NONE, index + 1);
            } else {
                return;
            }
            newTableItem.setText(tableItemContent);

            Exercise exercise = parseTableItemToExercise(newTableItem);
            String logMessage = String.format("Aufgabe \"%s\" verschoben", exercise.getName());
            log.addMessage(logMessage);

            btnOpenExcel.deactivate();
        }
    }

    @Override
    public void publishUpdate(String message, LogType logType) {
        addLogMessage(message, logType);
    }

    @Override
    public Shell getShell() {
        if (this.shell != null && !this.shell.isDisposed()) {
            return this.shell;
        } else {
            return null;
        }
    }
}
