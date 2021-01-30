import React, { useEffect, useRef, useState } from "react";
import { Button, Carousel, Col, Modal, Row } from "antd";
import { getUnreadAnnouncements } from "../../apis/announcements/announcements";
import { IconLeft, IconRight } from "../icons/Icons";

export function Announcements() {
  const [announcements, setAnnouncements] = useState([]);
  const [visible, setVisibility] = useState(true);
  const slider = useRef(null);

  useEffect(() => {
    getUnreadAnnouncements().then((data) => {
      console.log(data.data);
      setAnnouncements(data.data);
    });
  }, []);

  const contentStyle = {
    height: "160px",
    color: "#fff",
    lineHeight: "160px",
    textAlign: "center",
    background: "#364d79",
  };

  return (
    <>
      <Modal
        title="High Priority Announcements"
        onCancel={() => setVisibility(false)}
        visible={visible}
        footer={null}
      >
        <Row justify="space-between" align="middle">
          <Col span={2} style={{ textAlign: "right" }}>
            <Button
              icon={<IconLeft />}
              shape="circle"
              onClick={() => slider.current.prev()}
              style={{ border: "none" }}
            />
          </Col>
          <Col span={20}>
            <Carousel ref={slider} effect="fade" dots={false}>
              <div>
                <h3 style={contentStyle}>1</h3>
              </div>
              <div>
                <h3 style={contentStyle}>2</h3>
              </div>
              <div>
                <h3 style={contentStyle}>3</h3>
              </div>
              <div>
                <h3 style={contentStyle}>4</h3>
              </div>
            </Carousel>
          </Col>
          <Col span={2} style={{ textAlign: "left" }}>
            <Button
              icon={<IconRight />}
              shape="circle"
              onClick={() => slider.current.next()}
              style={{ border: "none" }}
            />
          </Col>
        </Row>
      </Modal>
    </>
  );
}
