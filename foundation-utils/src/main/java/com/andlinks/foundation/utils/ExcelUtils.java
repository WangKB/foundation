package com.andlinks.foundation.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 王凯斌 on 2017/6/17.
 */
public class ExcelUtils {

    private final static int page = 10000;

    /**
     * 创建excel
     *
     * @param fileName
     * @param title
     * @param data
     * @throws Exception
     */
    public static String createExcel(String fileName, List<String> title, List<List<String>> data, String excelPath) throws Exception {

        Workbook wb = new HSSFWorkbook();

        int sheetNum = (data.size() % page == 0) ? (data.size() / page) : (data.size() / page) + 1;
        int eachSheetNum = 0;
        for (int sheetIndex = 0; sheetIndex < sheetNum; sheetIndex++) {
            Sheet sheet = wb.createSheet(fileName + sheetIndex);
            Row titleRow = sheet.createRow((short) 0);
            eachSheetNum = ((sheetIndex + 1) == sheetNum) ? ((data.size() % page == 0) ? page : (data.size() % page)) : page;
            fillRowWithCells(titleRow, title);
            for (int n = 0; n < eachSheetNum; n++) {
                Row dataRow = sheet.createRow(n + 1);
                fillRowWithCells(dataRow, data.get(n));
            }
        }
        String fileResult = createExcelByWorkBook(fileName, wb, excelPath);
        return fileResult;
    }

    /**
     * 创建带cell的row
     *
     * @param row
     * @param cells
     * @return
     */
    private static Row fillRowWithCells(Row row, List<String> cells) {
        for (int n = 0; n < cells.size(); n++) {
            Cell cell = row.createCell(n);
            cell.setCellValue(cells.get(n));
        }
        return row;
    }

    public static List<String> getSingleCol(File file, int sheetIndex, int colIndex) {

        List<String> list = new ArrayList<>();
        try {
            FileInputStream excelFile = new FileInputStream(file);
            Workbook workbook = new HSSFWorkbook(excelFile);
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            int rows = sheet.getLastRowNum() + 1;
            for (int row = 0; row < rows; row++) {
                Row r = sheet.getRow(row);
                list.add(r.getCell(colIndex).getStringCellValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 用Map替代List表达每行的数据
     *
     * @param fileName
     * @param title
     * @param data
     * @throws Exception
     */
    public static String createExcelWithMap(String fileName, List<String> title, List<Map> data, String excelPath) throws Exception {

        List<List<String>> newData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            newData.add(new ArrayList<>(data.get(i).values()));
        }
        return createExcel(fileName, title, newData, excelPath);
    }

    public static <T> String createExcelWithEntity(String fileName, List<String> title, List<T> data, String[] attrs, String excelPath) throws Exception {

        List<List<String>> newData = new ArrayList<>();
        for (T item : data) {
            List<String> row = createRow(item, attrs);
            newData.add(row);
        }
        return createExcel(fileName, title, newData, excelPath);
    }

    private static <T> List<String> createRow(T item, String[] attrs) throws Exception {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(item.getClass().getSuperclass().getDeclaredFields()));
        fields.addAll(Arrays.asList(item.getClass().getDeclaredFields()));

        List<String> row = new ArrayList<>();
        for (String attr : attrs) {
            for (Field field : fields) {
                if (field.getName().equals(attr)) {

                    row.add(getFieldValue(item, field));
                }
            }
        }
        return row;
    }

    private static String getFieldValue(Object item, Field field) throws Exception {
        field.setAccessible(true);
        String value = "";
        value = String.valueOf(field.get(item));
        if (field.get(item) == null) {
            return "";
        }
        if (Date.class.equals(field.getType())) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(field.get(item));
        }
        if (Boolean.class.equals(field.getType())) {
            return (Boolean) field.get(item) ? "是" : "否";
        }
        return value;
    }


    public static String createExcelByWorkBook(String fileName, Workbook workbook, String excelPath) throws Exception {
        File parentFile = new File(excelPath);
        File excelFile = new File(parentFile, fileName + ".xls");
        FileOutputStream fileOut = new FileOutputStream(excelFile);
        workbook.write(fileOut);
        fileOut.close();
        return fileName + ".xls";
    }

    public static void createExcelByWorkBookByBytes(String fileName, Workbook workbook, HttpServletResponse response) throws Exception {
        response.reset();
        //ByteArrayOutputStream os = new ByteArrayOutputStream();
        //workbook.write(os);
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + new String((fileName + ".xls").getBytes("GBK"), "iso-8859-1"));
        workbook.write(response.getOutputStream());
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }
}
