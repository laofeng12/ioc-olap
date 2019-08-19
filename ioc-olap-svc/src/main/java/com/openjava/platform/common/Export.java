package com.openjava.platform.common;

import com.openjava.platform.mapper.kylin.ColumnMetaMapper;
import com.openjava.platform.mapper.kylin.QueryResultMapper;
import com.openjava.platform.vo.AnyDimensionCellVo;
import com.openjava.platform.vo.AnyDimensionVo;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Export {
    public static void dualDate(QueryResultMapper queryResult, HttpServletResponse response) throws Exception {
        ArrayList<ColumnMetaMapper> columnMetas = queryResult.columnMetas;//获取到的columnMetas数据

        String[] fieldName = new String[columnMetas.size()];//列头名称
        String[] columnIt = new String[columnMetas.size()];//列key
        for (int i = 0; i < columnMetas.size(); i++) {//columnMetas 的循环
            Object fieldNamee = columnMetas.get(i);  //一行数据
            fieldName[i] = ((ColumnMetaMapper) fieldNamee).label.toString();//列头名称    数据
            columnIt[i] = ((ColumnMetaMapper) fieldNamee).name.toString();//列key      数据
        }

        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();//数据   格式为List<Map<String, Object>>
        ArrayList<ArrayList<String>> results = queryResult.results;//获取到的results数据

        for (int m = 0; m < results.size(); m++) {//最外层 循环
            Object date = results.get(m);  //获取一行数据
            Map<String, Object> map = new HashMap<String, Object>();
            for (int n = 0; n < ((ArrayList) date).size(); n++) {
                map.put(columnIt[n], ((ArrayList) date).get(n).toString());//获取一行数据 的具体一个数据
            }
            listMap.add(map);//数据集
        }

        String fileName = "导出数据" + System.currentTimeMillis();
        getExportedFile(fileName, response);
        exportExcel(listMap, fieldName, columnIt, "数据sheet", listMap.size(), response.getOutputStream());
    }

    public static void setFileDownloadHeader(HttpServletRequest request, HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
        final String userAgent = request.getHeader("USER-AGENT");
        String finalFileName = null;
        if (StringUtils.countOccurrencesOf(userAgent, "MSIE") > 0) {// IE浏览器
            // MSIE
            finalFileName = URLEncoder.encode(fileName, "UTF8");
        } else if (StringUtils.countOccurrencesOf(userAgent, "Mozilla") > 0) {// google,火狐浏览器
            // Mozilla
            finalFileName = new String(fileName.getBytes(), "ISO8859-1");
        } else {
            finalFileName = URLEncoder.encode(fileName, "UTF8");// 其他浏览器
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"" + finalFileName + "\"");// 这里设置一下让浏览器弹出下载提示框，而不是直接在浏览器中打开
    }

    /**
     * 导出到excel
     *
     * @param list
     * @param fieldName
     * @param columnIt
     * @param sheetName
     * @param sheetSize
     * @param output
     * @return boolean
     * @Title: exportExcel
     */
    @SuppressWarnings({"rawtypes"})
    public static boolean exportExcel(List list, String[] fieldName, Object[] columnIt, String sheetName, Integer sheetSize, OutputStream output) throws IOException {
        // 产生工作薄对象
        XSSFWorkbook workbook = new XSSFWorkbook();
        if (sheetSize >= 65536) {
            sheetSize = 65536;
        }
        double sheetNo = Math.ceil(list.size() / sheetSize);// 计算需要几个sheet
        for (int index = 0; index < sheetNo; index++) {
            // 产生工作表对象
            XSSFSheet sheet = workbook.createSheet();
            // 设置工作表的名称.
            workbook.setSheetName(index, sheetName + (index + 1));
            //默认宽度
            sheet.setDefaultColumnWidth(15);
            //默认高度
            //sheet.setDefaultRowHeight((short) 15);
            // 产生一行
            XSSFRow row = sheet.createRow(0);
            // 产生单元格
            XSSFCell cell;


            XSSFCellStyle cellStyle = getTitleStyle(workbook);
            // 写入各个字段的名称
            for (int i = 0; i < fieldName.length; i++) {
                // 创建第一行各个字段名称的单元格
                cell = row.createCell(i);
                // 设置单元格内容为字符串型
                //cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyle);
                // 给单元格内容赋值
                cell.setCellValue(fieldName[i]);

            }

            int startNo = index * sheetSize;
            int endNo = Math.min(startNo + sheetSize, list.size());
            // 写入各条记录,每条记录对应excel表中的一行
            for (int i = startNo; i < endNo; i++) {
                row = sheet.createRow(i + 1 - startNo);
                HashMap map = (HashMap) list.get(i);
                for (int j = 0; j < columnIt.length; j++) {
                    cell = row.createCell(j);
                    //cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                    Object value = map.get(columnIt[j]);
                    if (value != null) {
                        cell.setCellValue(map.get(columnIt[j]).toString());
                    } else
                        cell.setCellValue("");
                }
            }
        }

        workbook.write(output);
        output.flush();
        output.close();
        return true;
    }

    /**
     * 标题单元格样式
     *
     * @param workbook
     * @return CellStyle
     * @Title: getTitleStyle
     */
    public static XSSFCellStyle getTitleStyle(XSSFWorkbook workbook) {
        XSSFCellStyle titleStyel = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        titleStyel.setFont(font);
        return titleStyel;
    }

    public static void getExportedFile(String name, HttpServletResponse response) throws Exception {
        String fileName = new String((name + ".xlsx").getBytes("UTF-8"), "iso-8859-1");
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName); //文件名
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
    }

    public static boolean dualAnyDimensionVoDate(AnyDimensionVo anyDimensionVo, HttpServletResponse response) throws Exception {
        List<ArrayList<AnyDimensionCellVo>> results = anyDimensionVo.getResults();//获取到的results数据
        String fileName = "olap分析数据" + System.currentTimeMillis();
        getExportedFile(fileName, response);

        String sheetName = "olap分析数据";
        Integer sheetSize = results.size();

        // 产生工作薄对象
        XSSFWorkbook workbook = new XSSFWorkbook();
        if (sheetSize >= 65536) {
            sheetSize = 65536;
        }
        double sheetNo = Math.ceil(results.size() / sheetSize);// 计算需要几个sheet

        for (int index = 0; index < sheetNo; index++) {
            // 产生工作表对象
            XSSFSheet sheet = workbook.createSheet();
            // 设置工作表的名称.
            workbook.setSheetName(index, sheetName + (index + 1));
            //默认宽度
            sheet.setDefaultColumnWidth(10);
            //默认高度
            //sheet.setDefaultRowHeight((short) 15);
            // 产生一行
            XSSFRow row = sheet.createRow(0);
            // 产生单元格
            XSSFCell cell;

            int startNo = index * sheetSize;
            int endNo = Math.min(startNo + sheetSize, results.size());

            // 写入各条记录,每条记录对应excel表中的一行
            for (int i = startNo; i < endNo; i++) {
                row = sheet.createRow(i - startNo);
                ArrayList<AnyDimensionCellVo> anyDimensionCellVoL = results.get(i);//一行数据
                int addCol = 0;
                int endrow = 0;
                for (int j = 0; j < anyDimensionCellVoL.size(); j++) {
                    XSSFCellStyle cellStyle = workbook.createCellStyle();
                    XSSFFont font = workbook.createFont();
                    Integer startrow = i - startNo;
                    Integer overrow = i - startNo;
                    Integer startcol = j + addCol;
                    Integer overcol = j;
                    cell = row.createCell(j + addCol);
                    AnyDimensionCellVo anyDimensionCellVoD = anyDimensionCellVoL.get(j);
                    String date = anyDimensionCellVoD.getValue();
                    if (null == date || "null".equals(date) || "".equals(date)) {
                        cell.setCellValue("");//单元格写入数据
                    } else
                        cell.setCellValue(date);//单元格写入数据
                    if (anyDimensionCellVoD.getType() == 1 || anyDimensionCellVoD.getType() == 2 || anyDimensionCellVoD.getType() == 3) {
                        cellStyle.setAlignment(HorizontalAlignment.CENTER);
                        font.setFontName("黑体");
                        //font.setBold(true);//粗体显示
                        cellStyle.setFont(font);
                        cell.setCellStyle(cellStyle);
                    }

                    int rowspan = anyDimensionCellVoD.getRowspan();
                    int colspan = anyDimensionCellVoD.getColspan();
                    if (anyDimensionCellVoD.getType() == 1 && colspan > 1) {//行维   ||anyDimensionCellVoD.getRowspan()>1
                        for (int jj = 1; jj < colspan; jj++) {
                            addCol++;
                            cell = row.createCell(j + addCol);
                            cell.setCellValue("");//单元格写入数据
                            overcol = j + addCol;
                        }
                        sheet.addMergedRegion(new CellRangeAddress(startrow, overrow, startcol, overcol));
                    }
                    if (anyDimensionCellVoD.getType() == 2 && rowspan > 1) {//列维   anyDimensionCellVoD 一行数据     ||anyDimensionCellVoD.getColspan()>1
                        AnyDimensionCellVo anyDimensionCellVoLast = new AnyDimensionCellVo();//上一行数据 列维相同的数据
                        AnyDimensionCellVo anyDimensionCellVoNext = new AnyDimensionCellVo();//下一行数据 列维相同的数据
                        if (i - 1 >= 0) {
                            ArrayList<AnyDimensionCellVo> anyDimensionCellVoLLast = results.get(i - 1);//上一行数据
                            anyDimensionCellVoLast = anyDimensionCellVoLLast.get(j);//列维相同的数据
                        }
                        if (i + 1 < results.size()) {
                            ArrayList<AnyDimensionCellVo> anyDimensionCellVoLNext = results.get(i + 1);//下一行数据
                            anyDimensionCellVoNext = anyDimensionCellVoLNext.get(j);//列维相同的数据
                        }
                        String lastDate = anyDimensionCellVoLast.getValue();//判断是不是第一行
                        if (lastDate != null) {
                            if ((lastDate.equals(date) && (!anyDimensionCellVoD.getValue().equals(anyDimensionCellVoNext.getValue())))
                                    || (lastDate.equals(date) && ((i + 1) == results.size()))) {
                                int endRow = (int) overrow;
                                Integer firstRow = endRow - rowspan + 1;
                                sheet.addMergedRegion(new CellRangeAddress(firstRow, overrow, startcol, overcol));
                            }
                        }
                    }
                }
            }
        }
        workbook.write(response.getOutputStream());
        response.getOutputStream().flush();
        response.getOutputStream().close();
        return true;
    }

}
