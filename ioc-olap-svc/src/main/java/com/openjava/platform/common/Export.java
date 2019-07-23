package com.openjava.platform.common;

import com.openjava.platform.mapper.kylin.ColumnMetaMapper;
import com.openjava.platform.mapper.kylin.QueryResultMapper;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Export {
    public static void   dualDateExportExcel(QueryResultMapper queryResult, HttpServletResponse response) {
        ArrayList<ColumnMetaMapper> columnMetas=queryResult.columnMetas;//获取到的columnMetas数据

        String[] fieldName = new String[columnMetas.size()];//列头名称
        String[] columnIt = new String[columnMetas.size()];//列key
        for (int i = 0; i < columnMetas.size(); i++) {//columnMetas 的循环
            Object fieldNamee =columnMetas.get(i);  //一行数据
            fieldName[i]= ((ColumnMetaMapper) fieldNamee).label.toString();//列头名称    数据
            columnIt[i]= ((ColumnMetaMapper) fieldNamee).name.toString();//列key      数据
        }

        List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();//数据   格式为List<Map<String, Object>>
        ArrayList<ArrayList<String>> results=queryResult.results;//获取到的results数据

        for(int ii=0;ii<results.size();ii++) {//最外层 循环
            Object date =results.get(ii);  //获取一行数据
            Map<String, Object> map = new HashMap<String, Object>();
            for (int iii = 0; iii < ((ArrayList) date).size(); iii++) {
                map.put(columnIt[iii], ((ArrayList) date).get(iii).toString());//获取一行数据 的具体一个数据
            }
            listMap.add(map);//数据集
        }

        try {
            String fileName = "导出数据"+ System.currentTimeMillis()+ ".xls";
            response.setContentType("text/html; charset=utf-8");
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("GB2312"), "ISO-8859-1"));
            exportExcel(listMap, fieldName, columnIt, "数据sheet", listMap.size(), response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void setFileDownloadHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
        final String userAgent = request.getHeader("USER-AGENT");
        try {
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
        } catch (UnsupportedEncodingException e) {
        }
    }

    /**
     * 导出到excel
     *
     * @Title: exportExcel
     * @param list
     * @param fieldName
     * @param columnIt
     * @param sheetName
     * @param sheetSize
     * @param output
     * @return boolean
     */
    @SuppressWarnings({ "rawtypes" })
    public static boolean exportExcel(List list, String[] fieldName, Object[] columnIt, String sheetName, Integer sheetSize, OutputStream output) {
        // 产生工作薄对象
        HSSFWorkbook workbook = new HSSFWorkbook();
        if (sheetSize >= 65536) {
            sheetSize = 65536;
        }
        double sheetNo = Math.ceil(list.size() / sheetSize);// 计算需要几个sheet
        for (int index = 0; index < sheetNo; index++) {
            // 产生工作表对象
            HSSFSheet sheet = workbook.createSheet();
            // 设置工作表的名称.
            workbook.setSheetName(index, sheetName + (index + 1));
            //默认宽度
            sheet.setDefaultColumnWidth(15);
            //默认高度
            //sheet.setDefaultRowHeight((short) 15);
            // 产生一行
            HSSFRow row = sheet.createRow(0);
            // 产生单元格
            HSSFCell cell;


            CellStyle cellStyle = getTitleStyle(workbook);
            // 写入各个字段的名称
            for (int i = 0; i < fieldName.length; i++) {
                // 创建第一行各个字段名称的单元格
                cell = row.createCell(i);
                // 设置单元格内容为字符串型
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
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
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    Object value = map.get(columnIt[j]);
                    if (value != null) {
                        cell.setCellValue(map.get(columnIt[j]).toString());
                    } else
                        cell.setCellValue("");
                }
            }
        }
        try {
            workbook.write(output);
            output.flush();
            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 标题单元格样式
     *
     * @Title: getTitleStyle
     * @param workbook
     * @return CellStyle
     */
    public static CellStyle getTitleStyle(Workbook workbook) {
        CellStyle titleStyel = workbook.createCellStyle();
        HSSFFont font = (HSSFFont) workbook.createFont();
        //font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 设置粗体
        titleStyel.setFont(font);
        return titleStyel;
    }

    public static void getExportedFile(XSSFWorkbook workbook, String name, HttpServletResponse response) throws Exception {
        BufferedOutputStream fos = null;
        try {
            String fileName = name + ".xlsx";
            fileName = new String(fileName.getBytes(),"ISO8859-1");
            response.setContentType("application/x-msdownload");//".xls"="application/vnd.ms-excel"  //application/x-msdownload
            response.setHeader("Content-Disposition","attachment;fileName=" +fileName);
            fos = new BufferedOutputStream(response.getOutputStream());
            workbook.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }
}