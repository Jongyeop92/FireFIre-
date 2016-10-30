# -*- coding: utf8 -*-


# sms module
import sys
sys.path.append('..')
import my_sms_api

# push alarm module
from pyfcm import FCMNotification
import requests


# sms init data
SENDER = '01026249390'
NUMBER_OF_119 = '01026249390'

# push alarm init data
PUSH_API_KEY = 'AIzaSyCulJFPCccu7rC95JJxgvemkbDZYrx6GV0'
SERIAL_NUMBER = '1000'
SERVER_URL = 'https://morning-depths-56555.herokuapp.com/'


def notifyFire():
    # send push alarm
    push_service = FCMNotification(api_key=PUSH_API_KEY, proxy_dict={})

    r = requests.get(SERVER_URL + 'api/tokenLists/' + SERIAL_NUMBER)
    token_list = r.json()['token_list']

    r = requests.get(SERVER_URL + 'api/users/by_serial_number/' + SERIAL_NUMBER)
    user_data = r.json()

    for token in token_list:
        r = requests.get(SERVER_URL + 'api/user_push_data/' + token)
        user_push_data = r.json()['push_data']

        nickname = ''
        for push_data in user_push_data:
            if push_data['serial_number'] == SERIAL_NUMBER:
                nickname = push_data['nickname']
                break

        data = {
            "title": "Fire!",
            "message": u"%s(%s) 에서 화재 발생!" % (user_data['address'], nickname),
            "date": time.strftime('%Y-%m-%d %H:%M')
        }

        result = push_service.notify_single_device(registration_id=token, data_message=data)
        print result
        
    # send sms

    # to user
    message = '%s 에서 화재 발생!' % (user_data['address'].encode('utf8'))
    sms_number_list = user_data['sms_number_list']
    for sms_number in sms_number_list:
        my_sms_api.send_sms(sms_number, message, SENDER)

    # to 119
    message = u"%s 에서 화재 발생! 추가 정보: %s" %(user_data['address'].encode('utf8'), user_data['additional_info'].encode('utf8'))
    my_sms_api.send_sms(NUMBER_OF_119, message, SENDER)

def main():
    notifyFire()


if __name__ == '__main__':
    main()
    sys.exit(0)
