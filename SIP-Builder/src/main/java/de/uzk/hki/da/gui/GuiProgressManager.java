package de.uzk.hki.da.gui;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import de.uzk.hki.da.sb.ProgressManager;
import de.uzk.hki.da.sb.SIPCreationJob;

/**
 * A specialized progress manager responsible for updating the progress bar in GUI mode.
 * Uses GuiTheme styling for consistent appearance.
 *
 * @author Thomas Kleinke
 */
class GuiProgressManager extends ProgressManager {

    private JProgressBar progressBar;
    private JLabel progressDisplay;
    private JLabel stepDisplay;

    public GuiProgressManager(JProgressBar progressBar, JLabel progressDisplay, JLabel stepDisplay) {
        this.progressBar = progressBar;
        this.progressDisplay = progressDisplay;
        this.stepDisplay = stepDisplay;
    }

    @Override
    public void reset() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                progressBar.setEnabled(true);
                progressBar.setIndeterminate(false);
                progressBar.setValue(0);
                stepDisplay.setText("");
            }
        });
        super.reset();
    }

    @Override
    public void abort() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                progressBar.setEnabled(false);
                progressBar.setIndeterminate(false);
                if (progressBar.getValue() == 100) {
                    progressBar.setValue(progressBar.getValue() - 1);
                    progressBar.setValue(progressBar.getValue() + 1);
                } else {
                    progressBar.setValue(progressBar.getValue() + 1);
                    progressBar.setValue(progressBar.getValue() - 1);
                }
                progressDisplay.setText("SIP-Erstellungsvorgang abgebrochen");
                stepDisplay.setText("");
            }
        });
    }

    @Override
    public void createStartMessage() {
    }

    @Override
    public void startJob(int id) {
        SIPCreationJob job = jobMap.get(id);
        job.initialTotalProgress = totalProgress;

        final int jobId = id;
        final String packageName = job.packageName;

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (jobId == -1)
                    progressDisplay.setText("Erstelle Lieferung " + "\"" + packageName + "\"");
                else
                    progressDisplay.setText("Erstelle SIP aus Ordner " + "\"" + packageName + "\"");
            }
        });
    }

    @Override
    public void copyProgress(int id, long processedData) {
        super.copyProgress(id, processedData);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue((int) totalProgress);
                progressBar.setIndeterminate(false);
                stepDisplay.setText("Kopiere Daten...");
            }
        });
    }

    @Override
    public void premisProgress(int id, double progress) {
        super.premisProgress(id, progress);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue((int) totalProgress);
                progressBar.setIndeterminate(false);
                stepDisplay.setText("Erzeuge Premis-Datei...");
            }
        });
    }

    @Override
    public void bagitProgress(int id, double progress) {
        super.bagitProgress(id, progress);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue((int) totalProgress);
                progressBar.setIndeterminate(true);
                stepDisplay.setText("Erstelle BagIt...");
            }
        });
    }

    @Override
    public void archiveProgress(int id, long archivedData) {
        super.archiveProgress(id, archivedData);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue((int) totalProgress);
                progressBar.setIndeterminate(false);
                stepDisplay.setText("Erstelle Archivdatei...");
            }
        });
    }

    @Override
    public void deleteTempProgress(int id, double progress) {
        super.deleteTempProgress(id, progress);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue((int) totalProgress);
                progressBar.setIndeterminate(false);
                stepDisplay.setText("Loesche temporaere Daten...");
            }
        });
    }

    @Override
    public void skipJob(int id) {
        super.skipJob(id);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue((int) totalProgress);
                progressBar.setIndeterminate(false);
            }
        });
    }

    @Override
    public void createSuccessMessage(boolean skippedFiles) {
        super.createSuccessMessage(skippedFiles);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue((int) totalProgress);
                progressBar.setIndeterminate(false);
                stepDisplay.setText("");
                progressDisplay.setText("Die SIP-Erstellung wurde erfolgreich abgeschlossen.");
            }
        });
    }
}
