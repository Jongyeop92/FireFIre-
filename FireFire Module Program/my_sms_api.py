# -*- coding: utf8 -*-

import requests
import urllib

username = 'h920915'
key = 'V6zsxXoLnSTFEBJ'

url = 'https://directsend.co.kr/index.php/api/v1/sms'

def send_sms(to, message, sender):
    message = unicode(message, 'utf8').encode('euc-kr').encode('base64')

    url_parameters = {'message'    : message,
                      'sender'     : sender,
                      'username'   : username,
                      'recipients' : to,
                      'key'        : key
                      }

    r = requests.post(url, data=url_parameters)

    print r.content


def main():
    to = '01026249390'
    message = '문자 테스트입니다.'
    sender = '01026249390'
    
    send_sms(to, message, sender)

if __name__ == '__main__':
    main()
