import logging


# 日志配置信息   定义颜色  配置控制台日志处理器
class ColorFormatter(logging.Formatter):
    """扩展 logging.Formatter 以添加颜色"""
    grey = "\x1b[;11m"
    green = "\x1b[32;11m"  # 绿色
    yellow = "\x1b[33;11m"
    red = "\x1b[31;11m"
    bold_red = "\x1b[41;1m"
    reset = "\x1b[0m"  # 重置色
    format = "%(levelname)s: %(message)s (%(asctime)s; %(filename)s:%(lineno)d)"

    FORMATS = {
        logging.DEBUG: grey + format + reset,
        logging.INFO: green + format + reset,
        logging.WARNING: yellow + format + reset,
        logging.ERROR: red + format + reset,
        logging.CRITICAL: bold_red + format + reset
    }

    def format(self, record):
        log_fmt = self.FORMATS.get(record.levelno)
        formatter = logging.Formatter(log_fmt)
        return formatter.format(record)


# 创建一个流处理器（控制台输出）
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)  # 控制台输出 级别以上日志
ch.setFormatter(ColorFormatter())

LOG_FORMAT = "%(asctime)s - %(levelname)s - %(message)s"
logging.basicConfig(level=logging.DEBUG, format=LOG_FORMAT, handlers=[ch])
log = logging.getLogger(__name__)
