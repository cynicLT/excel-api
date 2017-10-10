package org.cynic.excel.service.manager;

import org.apache.commons.lang3.tuple.Pair;
import org.cynic.excel.config.DataItem;
import org.cynic.excel.config.RuleValues;

import java.util.List;

class XlsFileManager implements FileManager {
    //    TODO: implement using https://www.concretepage.com/apache-api/read-write-and-update-xlsx-using-poi-in-java
    @Override
    public List<String> readConstraintValues(List<DataItem> items, byte[] source) {
        return null;
    }

    @Override
    public List<Pair<DataItem, List<String>>> readSourceData(List<RuleValues> values, byte[] source) {
        return null;
    }

    @Override
    public byte[] pasteReadData(List<Pair<DataItem, List<String>>> readData, byte[] destination) {
        return new byte[0];
    }
}
