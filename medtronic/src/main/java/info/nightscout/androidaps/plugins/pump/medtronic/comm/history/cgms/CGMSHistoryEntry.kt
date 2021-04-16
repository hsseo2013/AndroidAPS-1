package info.nightscout.androidaps.plugins.pump.medtronic.comm.history.cgms

import info.nightscout.androidaps.plugins.pump.common.utils.ByteUtil
import info.nightscout.androidaps.plugins.pump.common.utils.DateTimeUtil
import info.nightscout.androidaps.plugins.pump.medtronic.comm.history.MedtronicHistoryEntry
import org.apache.commons.lang3.StringUtils
import org.joda.time.LocalDateTime

/**
 * This file was taken from GGC - GNU Gluco Control (ggc.sourceforge.net), application for diabetes
 * management and modified/extended for AAPS.
 *
 * Author: Andy {andy.rozman@gmail.com}
 */
class CGMSHistoryEntry : MedtronicHistoryEntry() {

    var entryType: CGMSHistoryEntryType? = null
        private set

    override var opCode: Byte? = null // this is set only when we have unknown entry...
        get() = if (field == null) entryType!!.code.toByte() else field

    fun setEntryType(entryType: CGMSHistoryEntryType) {
        this.entryType = entryType
        sizes[0] = entryType.headLength
        sizes[1] = entryType.dateLength
        sizes[2] = entryType.bodyLength
    }

    override val entryTypeName: String
        get() = entryType!!.name

    override fun setData(listRawData: List<Byte>, doNotProcess: Boolean) {
        if (entryType!!.schemaSet) {
            super.setData(listRawData, doNotProcess)
        } else {
            rawData = listRawData
        }
    }

    override val dateLength: Int
        get() = entryType!!.dateLength

    fun hasTimeStamp(): Boolean {
        return entryType!!.hasDate()
    }

    override val toStringStart: String
        get() = ("CGMSHistoryEntry [type=" + StringUtils.rightPad(entryType!!.name, 18) + " ["
            + StringUtils.leftPad("" + opCode, 3) + ", 0x" + ByteUtil.getCorrectHexValue(opCode!!) + "]")

    fun setDateTime(timeStamp: LocalDateTime, getIndex: Int) {
        setAtechDateTime(DateTimeUtil.toATechDate(timeStamp.plusMinutes(getIndex * 5)))
    }
}