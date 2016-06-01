from gumpy.lib import enum
from gumpy.commons import sics
import time
from gumpy.lib import enum
from org.gumtree.gumnix.sics.control import ServerStatus
from gumpy.commons.logger import log

# parameter values for nguide
OUT = 'OUT'
Out = 'OUT'
out = 'OUT'
R100 = 'R100'
S40 = 'S40'

D90 = 'D90'
D80 = 'D80'
D70 = 'D70'
D60 = 'D60'
D50 = 'D50'
D40 = 'D40'
D30 = 'D30'
D20 = 'D20'
D10 = 'D10'

# parameter values for beam stops
IN = 'IN'
In = 'IN'

# parameter values for dhv controller
UP = 'UP'
Up = 'UP'
up = 'UP'
DOWN = 'DOWN'
Down = 'DOWN'
down = 'DOWN'
 
#D5, D7.5, D10, D12.5, D15, D17.5, D20, D20, D30, D40

__sampleMap__ = {
       0 : 250.000,
       1 : 210.500,
       2 : 168.375, 
       3 : 126.250,
       4 : 84.125,
       5 : 42.000,
       6 : -39.700,
       7 : -81.875,
       8 : -124.000,
       9 : -166.125,
       10 : -208.250,
       11 : -250.000
       }

def att_pos(val = None):
    if not val is None :
        sics.drive('att_pos', val)
    return sics.get_raw_value('att_pos')

def att(val = None):
    if not val is None :
        sics.drive('att', val)
    return sics.get_raw_value('att')

def nguide(val1, val2, val3):
    if not type(val1) is int or val1 < 0 or val1 > 8 :
        raise Exception, 'first parameter must be an integer between 0 and 8'
    v2 = str(val2).upper()
    if v2 != 'D10' and v2 != 'D20' and v2 != 'D40' and v2 != 'S40' and v2 != 'R100' :
        raise Exception, 'second parameter must select from the list [D10, D20, D40, S40, R100]'
    v3 = str(val3).upper()
    if v3 != 'D10' and v3 != 'D20' and v3 != 'D40' and v3 != 'S40' and v3 != 'R100' :
        raise Exception, 'third parameter must select from the list [D10, D20, D40, S40, R100]'
    return sics.get_raw_feedback('nguide ' + str(val1) + ' ' + str(v2) + ' ' + str(v3))

def sapmot(val = None):
    if not val is None:
        if type(val) is float or type(val) is int :
            val = 'D' + str(val)
        sics.run_command('pdrive sapmot ' + str(val))
    return sics.get_raw_value('posname sapmot', str)

def samx(val = None):
    if not val is None :
        sics.drive('samx', val)
    return sics.get_raw_value('samx') 

def samz(val = None):
    if not val is None :
        sics.drive('samz', val)
    return sics.get_raw_value('samz')

def som(val = None):
    if not val is None :
        sics.drive('som', val)
    return sics.get_raw_value('som')

def __cal_samx__(val):
    if val <=0 or val >= 11:
        raise Exception, 'sample number not supported, must be within 1 to 10, got ' + str(val)
    ival = int(val)
    if ival == val:
        return __sampleMap__[ival]
    else:
        return __sampleMap__[ival] + (__sampleMap__[ival + 1] - __sampleMap__[ival]) * (val - ival)
            
     
def sample(val = None):
    global __sampleMap__
    if not val is None :
        if not type(val) is int or not type(val) is float:
            val = float(str(val))
        if val <=0 or val >= 11:
            raise Exception, 'sample number not supported, must be within 1 to 10, got ' + str(val)
        else:
#            sics.drive('samx', __sampleMap__[round(val)])
            sics.drive('samx', __cal_samx__(val))
    raw = sics.get_raw_value('samx')
    samNum = -1;
    for i in xrange(len(__sampleMap__)) :
        if raw > __sampleMap__[i] :
            if i > 0 :
                samNum = i - (raw - __sampleMap__[i]) / (__sampleMap__[i - 1] - __sampleMap__[i])
            break
    if samNum < 0.05 or samNum > 10.95:
        samNum = -1
    return round(samNum, 1)

def det(val = None):
    if not val is None :
        dhv('DOWN')
        sics.drive('det', val)
        dhv('UP')
    return sics.get_raw_value('det')

def curtaindet(val = None):
    if not val is None :
        dhv('DOWN')
        sics.drive('curtaindet', val)
        dhv('UP')
    return sics.get_raw_value('curtaindet')

def curtainl(val = None):
    if not val is None :
        dhv('DOWN')
        sics.drive('curtainl', val)
        dhv('UP')
    return sics.get_raw_value('curtainl')

def curtainr(val = None):
    if not val is None :
        dhv('DOWN')
        sics.drive('curtainr', val)
        dhv('UP')
    return sics.get_raw_value('curtainr')

def curtainu(val = None):
    if not val is None :
        dhv('DOWN')
        sics.drive('curtainu', val)
        dhv('UP')
    return sics.get_raw_value('curtainu')

def curtaind(val = None):
    if not val is None :
        dhv('DOWN')
        sics.drive('curtaind', val)
        dhv('UP')
    return sics.get_raw_value('curtaind')

def dhv(val = None):
    if not val is None:
        if val.upper() == 'UP':
            dhv1_up = sics.get_raw_value('dhv1 upper')
            dhv2_up = sics.get_raw_value('dhv2 upper')
            res = sics.run_command('drive dhv1 ' + str(dhv1_up) + ' dhv2 ' + str(dhv2_up))
            if res.find('Full Stop') >= 0:
                raise Exception, res
        elif val.upper() == 'DOWN':
            dhv1_down = sics.get_raw_value('dhv1 lower')
            dhv2_down = sics.get_raw_value('dhv2 lower')
            res = sics.run_command('drive dhv1 ' + str(dhv1_down) + ' dhv2 ' + str(dhv2_down))
            if res.find('Full Stop') >= 0:
                raise Exception, res
    else:
        dhv1_up = sics.get_raw_value('dhv1 upper')
        dhv1_down = sics.get_raw_value('dhv1 lower')
        dhv1 = sics.get_raw_value('dhv1')
        dhv2_up = sics.get_raw_value('dhv2 upper')
        dhv2_down = sics.get_raw_value('dhv2 lower')
        dhv2 = sics.get_raw_value('dhv2')
        if abs(dhv1_up - dhv1) <= 5 and (dhv2_up - dhv2) <= 5:
            return 'UP'
        elif abs(dhv1 - dhv1_down) <= 5 and (dhv2 - dhv2_down) <= 5:
            return 'DOWN'
        else:
            return 'ERROR'
        
class DetectorSystem :
    def __init__(self):
        self.det = None
        self.curtaindet = None
        self.curtainl = None
        self.curtainr = None
        self.curtainu = None
        self.curtaind = None
        
    def clear(self):
        self.det = None
        self.curtaindet = None
        self.curtainl = None
        self.curtainr = None
        self.curtainu = None
        self.curtaind = None
        
    def multiDrive(self, det, curtaindet, curtainl, curtainr, curtainu, curtaind):
        if det is None and curtaindet is None and curtainl is None and curtainr is None \
                and curtainu is None and curtaind is None:
            return
        cmd = 'drive'
        if not det is None:
            cmd += ' det ' + str(det)
        if not curtaindet is None:
            cmd += ' curtaindet ' + str(curtaindet) 
        if not curtainl is None:
            cmd += ' curtainl ' + str(curtainl)
        if not curtainr is None:
            cmd += ' curtainr ' + str(curtainr)
        if not curtainu is None:
            cmd += ' curtainu ' + str(curtainu)
        if not curtaind is None:
            cmd += ' curtaind ' + str(curtaind)
        res = sics.run_command(cmd)
        if not res is None and res.find('Full Stop') >= 0:
            raise Exception, res
        
    def drive(self):
        try:
            if self.det is None and \
                    self.curtaindet is None and \
                    self.curtainl is None and \
                    self.curtainr is None and \
                    self.curtainu is None and \
                    self.curtaind is None:
                raise Exception, 'please set up Detector first.'
            else :
                dhv('DOWN')
                if self.det is None or self.curtaindet is None :
                    self.multiDrive(self.det, self.curtaindet, self.curtainl, \
                                    self.curtainr, self.curtainu, self.curtaind)
                else:
                    cur_det = det()
                    cur_curtaindet = curtaindet()
                    if self.curtaindet <= cur_curtaindet and self.det >= cur_det :
                        self.multiDrive(self.det, self.curtaindet, self.curtainl, \
                                    self.curtainr, self.curtainu, self.curtaind)
                    elif self.curtaindet > cur_curtaindet:
                        sics.drive('det', self.det)
                        self.multiDrive(None, self.curtaindet, self.curtainl, \
                                    self.curtainr, self.curtainu, self.curtaind)
                    else :
                        self.multiDrive(None, self.curtaindet, self.curtainl, \
                                    self.curtainr, self.curtainu, self.curtaind)
                        sics.drive('det', self.det)
                dhv('UP')
        finally:
            self.clear()

Detector = DetectorSystem()

def bs3(val = None):
    return __bs__(3, val)
    
def bs4(val = None):
    return __bs__(4, val)

def bs5(val = None):
    return __bs__(5, val)

def __bs__(id, val = None):
    bs_name = 'bs' + str(id)
    if not val is None:
        if type(val) is int or type(val) is float or str(val).isdigit():
            sics.drive(bs_name, val)
        elif str(val).upper() == 'IN':
            sics.drive(bs_name, 65)
        elif str(val).upper() == 'OUT':
            sics.drive(bs_name, 0)
    cur = sics.get_raw_value(bs_name)
    if cur >= 63 and cur <= 67:
        return 'IN'
    else :
        return 'OUT'    

def bs_att(bs_num, bs_angle, att_num):
    
    if type(bs_num) is str or type(bs_num) is unicode:
        bs_num = int(bs_num)
    
    if bs_num < 3 or bs_num > 5 :
        raise Exception, 'beam stop number ' + str(bs_num) + ' is not supported'

    if type(att_num) is str or type(att_num) is unicode:
        att_num = int(att_num)
        
    if att_num < 0 or att_num > 5 :
        raise Exception, 'att position number ' + str(att_num) + ' is not supported'
    
    if type(bs_angle) is str or type(bs_angle) is unicode:
        bs_angle = float(bs_angle)
    
    cur_bs3 = bs3()
    cur_bs4 = bs4()
    cur_bs5 = bs5()
    cur_att = att_pos()
     
    # Check if the current configuration has all beamstops out of position and the 
    # empty attenuator position selected. If this is the case, put an attenuator 
    # in (which will engage the fast shutter while doing so). 
    # Should not ever happen, but best to check.
    if (cur_bs3 < 63.0 or cur_bs3 > 67.0) and (cur_bs4 < 63.0 or cur_bs4  > 67.0) \
        and (cur_bs5 < 63.0 or cur_bs5 > 67.0) and cur_att == 3:
        log('put attenuator to 5 first')
        att_pos(5)

    # This is nominally the BS in position for all beamstops
    # Inserts a given beamstop and then remove all others. Then moves the attenuator.
    if bs_angle >= 63.0 and bs_angle <= 67.0 :
        log('put bs' + str(bs_num) + ' in')
        if bs_num == 3:
            sics.drive('bs3', bs_angle)
            sics.drive('bs4', 0)
            sics.drive('bs5', 0)
        elif bs_num == 4:
            sics.drive('bs4', bs_angle)
            sics.drive('bs3', 0)
            sics.drive('bs5', 0)
        elif bs_num == 5:
            sics.drive('bs5', bs_angle)
            sics.drive('bs3', 0)
            sics.drive('bs4', 0)
        log('put attenuator to ' + str(att_num))
        att_pos(att_num)
    # If you are driving an attenuator in, do this first, then move the beam stop
    elif (bs_angle < 63.0 or bs_angle > 67.0) and att_num != 3 :
        log('put attenuator to ' + str(att_num))
        att_pos(att_num)
        log('put bs' + str(bs_num) + ' in')
        if bs_num == 3:
            sics.drive('bs3', bs_angle)
            sics.drive('bs4', 0)
            sics.drive('bs5', 0)
        elif bs_num == 4:
            sics.drive('bs4', bs_angle)
            sics.drive('bs3', 0)
            sics.drive('bs5', 0)
        elif bs_num == 5:
            sics.drive('bs5', bs_angle)
            sics.drive('bs3', 0)
            sics.drive('bs4', 0)
    else:
        # Do not let the BS_Att command drive to an unsafe configuration
        raise Exception, 'No valid beamstop or attenuator has been selected  no movement of beamstop or attempted'

hmMode = enum.Enum('timer', 'monitor')
scanMode = enum.Enum('time', 'count', 'monitor', 'unlimited', 'MONITOR_1')
dataType = enum.Enum('HISTOGRAM_XYT')
saveType = enum.Enum('save', 'nosave')

# This scan rely on samx
def scan(deviceName, start, stop, numpoints, scanMode, dataType, preset, force='true', saveType=saveType.save):
    
    controllerPath = '/commands/scan/runscan'
    sicsController = sics.getSicsController()
    scanController = sicsController.findComponentController(controllerPath)
    
    sics.execute('hset ' + controllerPath + '/scan_variable ' + deviceName, 'scan')
    sics.execute('hset ' + controllerPath + '/scan_start ' + str(start), 'scan')
    sics.execute('hset ' + controllerPath + '/scan_stop ' + str(stop), 'scan')
    sics.execute('hset ' + controllerPath + '/numpoints ' + str(numpoints), 'scan')
    if (scanMode.key == 'monitor'):
        sics.execute('hset ' + controllerPath + '/mode MONITOR_1', 'scan')
    else:
        sics.execute('hset ' + controllerPath + '/mode ' + scanMode.key, 'scan')
    sics.execute('hset ' + controllerPath + '/preset ' + str(preset), 'scan')
    sics.execute('hset ' + controllerPath + '/datatype ' + dataType.key, 'scan')
    sics.execute('hset ' + controllerPath + '/savetype ' + saveType.key, 'scan')
    sics.execute('hset ' + controllerPath + '/force ' + force, 'scan')

    # repeat until successful
    while True:

        time.sleep(1)
        while not sics.getSicsController().getServerStatus().equals(ServerStatus.EAGER_TO_EXECUTE):
            time.sleep(0.1)
            
        scanController.syncExecute()
        break

    # Get output filename
    filenameController = sicsController.findDeviceController('datafilename')
    savedFilename = filenameController.getValue().getStringData()
    log('Saved to ' +  savedFilename)
    return savedFilename

def count(mode, dataType, preset, force='true', saveType=saveType.save):
    
    controllerPath = '/commands/scan/runscan'
    sicsController = sics.getSicsController()
    scanController = sicsController.findComponentController(controllerPath)
    
    sics.execute('hset ' + controllerPath + '/scan_variable ' + deviceName, 'scan')
    sics.execute('hset ' + controllerPath + '/scan_start 0', 'scan')
    sics.execute('hset ' + controllerPath + '/scan_stop 0', 'scan')
    sics.execute('hset ' + controllerPath + '/numpoints 1', 'scan')
    if (scanMode.key == 'monitor'):
        sics.execute('hset ' + controllerPath + '/mode MONITOR_1', 'scan')
    else:
        sics.execute('hset ' + controllerPath + '/mode ' + scanMode.key, 'scan')
    sics.execute('hset ' + controllerPath + '/preset ' + str(preset), 'scan')
    sics.execute('hset ' + controllerPath + '/datatype ' + dataType.key, 'scan')
    sics.execute('hset ' + controllerPath + '/savetype ' + saveType.key, 'scan')
    sics.execute('hset ' + controllerPath + '/force ' + force, 'scan')

    # repeat until successful
    while True:

        time.sleep(1)
        while not sics.getSicsController().getServerStatus().equals(ServerStatus.EAGER_TO_EXECUTE):
            time.sleep(0.1)
            
        scanController.syncExecute()
        break

    # Get output filename
    filenameController = sicsController.findDeviceController('datafilename')
    savedFilename = filenameController.getValue().getStringData()
    log('Saved to ' +  savedFilename)
    return savedFilename

def scan10(sample_position, collect_time, sample_name = None):
    global __sampleMap__
    if sample_position < 1 or sample_position > 10:
        raise Exception, 'Invalid sample position, scan not run. Choose a position between 1 and 10 inclusive.'
    else:
        if not sample_name is None:
            sics.execute('samplename ' + str(sample_name), 'scan')
        
        cur_samx = __sampleMap__[sample_position]
#        cur_samx = samx()
        time.sleep(1)
        log("Collection time set to " + str(collect_time) + " seconds")
#        sics.execute('histmem mode time')
#        sics.execute('histmem preset ' +  str(collect_time))
#        time.sleep(1)
        log("Data collection for sample " + sample_name + " started")
#        sics.execute("histmem start")
#        sicsController = sics.getSicsController()
#        while not sicsController.getServerStatus().equals(ServerStatus.COUNTING):
#                time.sleep(0.3)
#        while not sicsController.getServerStatus().equals(ServerStatus.EAGER_TO_EXECUTE):
#                time.sleep(0.3)
#        time.sleep(1)
#        sics.execute('newfile HISTOGRAM_XYT')
#        time.sleep(1)
#        log("Saving data")
#        sics.execute('save')
        scan('samx', cur_samx, cur_samx, 1, scanMode.time, dataType.HISTOGRAM_XYT, collect_time)
        time.sleep(2)
        log(sics.get_base_filename() + ' updated')
        log("Scan completed")
        if not sample_name is None:
            sics.execute('samplename {}', 'scan')
