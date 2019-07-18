#include <SoftwareSerial.h>
#include<LedControl.h>

int DIN = 7;  //dot 매트릭스 센서핀번호
int CS =  6;  //dot 매트릭스 센서핀번호
int CLK = 5;  //dot 매트릭스 센서핀번호

LedControl lc=LedControl(DIN,CLK,CS,0);   //8*8dot led조절변수

int blueTx=2;   //Tx (보내는핀 설정)at
int blueRx=3;   //Rx (받는핀 설정)
SoftwareSerial BTSerial(blueTx, blueRx);  //시리얼 통신을 위한 객체선언

int pin = 8;  //미세먼지 측정 pin번호
unsigned long duration;
unsigned long starttime;
unsigned long sampletime_ms = 15000;    //미세먼지 시간;
unsigned long lowpulseoccupancy = 0;    //미세먼지 값 측정에 활용되는 값
float ratio = 0;     //미세먼지 값 측정에 활용되는 값
int concentration = 0;    //미세먼지 값 저장
int LED_1 = 13;   //신호등 변수 선언
int LED_2 = 12;   //신호등 변수 선언
int LED_3 = 11;   //신호등 변수 선언

int i=0;    //앱으로 보내줄 신호등 변화값
void setup() {
 lc.shutdown(0,false);       //도트매트릭스 설정
 lc.setIntensity(0,15);      // 도트매트릭스 밝기 조절
 lc.clearDisplay(0);         //도트매트릭스 비우기
 
  
  BTSerial.begin(9600);   //블루투스 통신
  Serial.begin(9600);   //시리얼모니터 시작
  pinMode(8,INPUT);   
  starttime = millis();
  pinMode(LED_1, OUTPUT);   //신호등 설정해주기
  pinMode(LED_2, OUTPUT);   //신호등 설정해주기
  pinMode(LED_3, OUTPUT);   //신호등 설정해주기
}
 
void loop() {
  
  dust();   //미세먼지측정 함수
  
  BTSerial.println(concentration);  //미세먼지값을 블루투스를 통해 앱으로 전송
 
  byte smile[8]=   {0x3C,0x42,0xA5,0x81,0xA5,0x99,0x42,0x3C};   //도트 매트릭스 웃는 모양 16진수 변환
  byte neutral[8]= {0x3C,0x42,0xA5,0x81,0xBD,0x81,0x42,0x3C};   //도트 매트릭스 무표정 모양 16진수 변환
  byte frown[8]=   {0x3C,0x42,0xA5,0x81,0x99,0xA5,0x42,0x3C};   //도트 매트릭스 찡그리는 모양 16진수 변환


  
  digitalWrite(LED_3, LOW);
  digitalWrite(LED_1, HIGH);
  i=i+1;    //신호등 값을 보내주기위해 값변환
  i=i%3;    //빨간불일때 1을 의미하도록 하기위해서
  BTSerial.println(i);
  Serial.println(i);
  delay(5000);
   
  digitalWrite(LED_1, LOW);
  digitalWrite(LED_2, HIGH); 
  i=i+1;    //신호등 값을 보내주기위해 값변환
  i=i%3;    //노란불일때 2를 의미하도록 하기위해서
  BTSerial.println(i);
  Serial.println(i);
  delay(5000);
   
  digitalWrite(LED_2, LOW);
  digitalWrite(LED_3, HIGH);
  i=i+1;    //신호등 값을 보내주기위해 값변환
  i=i%3;    //초록불일때 3을 의미하도록 하기위해서
  BTSerial.println(i);
  Serial.println(i);
  delay(5000);
  

  



  if(concentration <30)      //미세먼지값이 30보다 작다면 웃는 모양 표시
  {
    printByte(smile);
  }

  else if(concentration<60 && concentration >=30)   //미세먼지값이 30보다 크고 60보다 작으면 무표정 모양 표시
  {
    printByte(neutral);
  }
  else    //미세먼지값이 60보다 크다면 화난 모양 표시
  {
    printByte(frown);
  }



  
}



//미세먼지 측정하기위한 함수
void dust()
{
  duration = pulseIn(pin, LOW);
    lowpulseoccupancy = lowpulseoccupancy+duration;

    if ((millis()-starttime) > sampletime_ms)
    {
        ratio = lowpulseoccupancy/(sampletime_ms*10.0);
        concentration = ((int)1.1*pow(ratio,3)-(int)3.8*pow(ratio,2)+(int)520*ratio+0.62)*100;
        Serial.println(concentration);
        lowpulseoccupancy = 0;
        starttime = millis();
    }
}




//도트매트릭스 출력해주기위한 설정한수
void printByte(byte character [])
{
  int i = 0;
  for(i=0;i<8;i++)
  {
    lc.setRow(0,i,character[i]);
  }
}

