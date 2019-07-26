package com.openjava.platform.common;

import com.openjava.platform.mapper.kylin.ColumnMetaMapper;
import com.openjava.platform.mapper.kylin.QueryResultMapper;
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

//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.ss.util.CellRangeAddress;

public class Export {
    public static void   dualDate(QueryResultMapper queryResult, HttpServletResponse response) {
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
            String fileName = "导出数据"+System.currentTimeMillis();;
            getExportedFile(fileName, response);
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
    public static XSSFCellStyle getTitleStyle(XSSFWorkbook workbook) {
        XSSFCellStyle titleStyel = workbook.createCellStyle();
        // CellStyle titleStyel = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        //font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);// 设置粗体
        titleStyel.setFont(font);
        return titleStyel;
    }

    public static void getExportedFile( String name, HttpServletResponse response) throws Exception {
        //BufferedOutputStream fos = null;
        //XSSFWorkbook workbook
        try {
            String fileName = name + ".xlsx";
            fileName = new String(fileName.getBytes(),"ISO8859-1");
            response.setContentType("application/x-msdownload");//".xls"="application/vnd.ms-excel"  //application/x-msdownload
            response.setHeader("Content-Disposition","attachment;fileName=" +fileName);
            //fos = new BufferedOutputStream(response.getOutputStream());
            //workbook.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //if (fos != null) {
            //    fos.close();
            //}
        }
    }

    public static boolean dualDateExportExcel2(HttpServletResponse response) throws Exception {
        String fileName = "测试合并"+System.currentTimeMillis();;
        getExportedFile(fileName,response);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("new sheet");

        //XSSFRow row = sheet.createRow(1);//行
       // XSSFCell cell = row.createCell(3);//列
        //cell.setCellValue("合并单元格");//赋值
        //XSSFCellStyle cellStyle = workbook.createCellStyle();
        //cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
        //cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
        //cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
        //cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
        //cellStyle.setAlignment(ALIGN_CENTER); // 居中

        //sheet.addMergedRegion(new CellRangeAddress(
        //        1,//第一行 （从0开始）
        //        2,//最后一行 （从0开始）
        //        3,//第一列 （从0开始）
        //        5//最后一列 （从0开始）
        //));
        //产生一行数据
        XSSFRow row = sheet.createRow(0);
        // 产生单元格
        XSSFCell cell;
/*        for (int j = 0; j < 100; j++) {
            row = sheet.createRow(j);
            for (int i = 0; i < 300; i++) {
                //cell = row.createCell(j);
                XSSFCell cell = row.createCell(0);
                cell.setCellValue("我是第:" + j + " 条数据,第:" + i + " 列数据");
            }
        }*/

        for (int i = 0; i < 3000; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 100; j++) {
                cell = row.createCell(j);
                if(i ==4&&j ==4){
                    cell.setCellValue("测试合并");
                }
                else if(i ==10&&j ==10){
                    cell.setCellValue("测试合并");
                }
                else {
                    cell.setCellValue("1");
                }
            }
        }
        sheet.addMergedRegion(new CellRangeAddress(//保存左上角的值
                4,//第一行 （从0开始）
                8,//最后一行 （从0开始）
                4,//第一列 （从0开始）
                8//最后一列 （从0开始）
        ));
        sheet.addMergedRegion(new CellRangeAddress(//保存左上角的值
                10,//第一行 （从0开始）
                13,//最后一行 （从0开始）
                10,//第一列 （从0开始）
                13//最后一列 （从0开始）
        ));
        try {
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
            response.getOutputStream().close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}