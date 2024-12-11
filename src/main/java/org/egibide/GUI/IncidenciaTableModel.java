package org.egibide.GUI;

import org.egibide.Modelo.Incidencia;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class IncidenciaTableModel extends AbstractTableModel {
    private String[] columns = {"Codigo", "Clasificación", "Asunto", "Lugar", "Descripción", "Tiempo estimado"};
    List<Incidencia> incidencias = new ArrayList<>();

    public IncidenciaTableModel(List<Incidencia> incidencias) {
        this.incidencias = incidencias;
        System.out.println("aqui");
    }

    @Override
    public int getRowCount() {
        return incidencias.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Incidencia current = incidencias.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return current.getCodigo();
            case 1:
                return current.getCategoria();
            case 2:
                return current.getAsunto();
            case 3:
                return current.getLugar();
            case 4:
                return current.getDescripcion();
            case 5:
                return current.getEstimacionTiempo();

        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }
}
