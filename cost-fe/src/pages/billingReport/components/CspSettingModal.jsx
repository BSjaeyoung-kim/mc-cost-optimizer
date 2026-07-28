import { useState, useEffect } from "react";
import Modal from "@/components/common/modal/Modal";
import Button from "@/components/common/button/Button";
import { startCurSetup, getCurSetupStatus } from "@/api/billing/curSetup";

const STEPS_ORDER = [
  "OpenBao permission check",
  "Root key retrieval",
  "CUR existing report check",
  "IAM setup & key issuance",
  "S3 bucket creation",
  "CUR report creation",
  "Dedicated key storage (cost/aws)",
  "DB registration",
];

const STATUS_COLOR = {
  OK:      "text-success",
  WARN:    "text-warning",
  SKIP:    "text-muted",
  FAILED:  "text-danger",
  PENDING: "text-secondary",
};

const STATUS_BADGE = {
  OK:      "badge bg-success text-white",
  WARN:    "badge bg-warning text-dark",
  SKIP:    "badge bg-secondary text-white",
  FAILED:  "badge bg-danger text-white",
  PENDING: "badge bg-light text-secondary border",
};

const STATUS_ICON = {
  OK:      "✓",
  WARN:    "!",
  SKIP:    "–",
  FAILED:  "✗",
  PENDING: "…",
};

export default function CspSettingModal({ open, onClose }) {
  const [csp, setCsp] = useState("aws");

  // idle | checking | configured | running | done | error
  const [phase, setPhase] = useState("idle");
  const [steps, setSteps] = useState([]);
  const [result, setResult] = useState(null);
  const [errorMsg, setErrorMsg] = useState("");
  const [currentStatus, setCurrentStatus] = useState(null); // { costCredsStored, dbRegistered }

  // 모달 열릴 때마다 현재 AWS 설정 상태 조회
  useEffect(() => {
    if (!open || csp !== "aws") return;
    setPhase("checking");
    getCurSetupStatus()
      .then((res) => {
        const s = res.data;
        setCurrentStatus(s);
        setPhase(s.costCredsStored && s.dbRegistered ? "configured" : "idle");
      })
      .catch(() => {
        setCurrentStatus(null);
        setPhase("idle");
      });
  }, [open, csp]);

  const resetAws = () => {
    setPhase("idle");
    setSteps([]);
    setResult(null);
    setErrorMsg("");
    setCurrentStatus(null);
  };

  const handleClose = () => {
    resetAws();
    setCsp("aws");
    onClose();
  };

  const handleCspChange = (next) => {
    if (next === csp) return;
    resetAws();
    setCsp(next);
  };

  const handleStart = async () => {
    setPhase("running");
    setSteps(STEPS_ORDER.map((name) => ({ name, status: "PENDING", message: null })));
    try {
      const res = await startCurSetup();
      const returned = res.data.steps || [];
      const merged = STEPS_ORDER.map((name) => {
        const found = returned.find((s) => s.name === name);
        return found || { name, status: "PENDING", message: null };
      });
      setSteps(merged);
      setResult(res.data);
      setPhase(merged.some((s) => s.status === "FAILED") ? "error" : "done");
    } catch (err) {
      const msg = err?.raw?.response?.data?.error || err?.userMessage || "An unknown error occurred.";
      setErrorMsg(msg);
      setPhase("error");
    }
  };

  const failedStep = steps.find((s) => s.status === "FAILED");

  const footer = (() => {
    if (csp !== "aws") return <Button variant="secondary" onClick={handleClose}>Close</Button>;
    if (phase === "checking") return <Button variant="secondary" disabled>Loading…</Button>;
    if (phase === "configured") return (
      <>
        <Button variant="secondary" onClick={handleClose}>Close</Button>
        <Button variant="outline-primary" onClick={() => setPhase("idle")}>Reconfigure</Button>
      </>
    );
    if (phase === "idle") return (
      <>
        <Button variant="secondary" onClick={handleClose}>Cancel</Button>
        <Button variant="primary" onClick={handleStart}>Start Setup</Button>
      </>
    );
    if (phase === "running") return <Button variant="secondary" disabled>In progress…</Button>;
    if (phase === "done") return <Button variant="primary" onClick={handleClose}>Done</Button>;
    // error
    return (
      <>
        <Button variant="secondary" onClick={handleClose}>Close</Button>
        <Button variant="primary" onClick={() => setPhase("idle")}>Retry</Button>
      </>
    );
  })();

  const title = phase === "done" ? "Cost Setup — Complete" : "Cost Setup";

  return (
    <Modal
      id="cspSettingModal"
      open={open}
      onClose={handleClose}
      title={title}
      size="md"
      centered
      statusColor={
        phase === "configured" || phase === "done" ? "success" :
        phase === "error" ? "danger" : undefined
      }
      footer={footer}
    >
      {/* CSP tabs */}
      <ul className="nav nav-tabs mb-3" style={{ borderBottom: "1px solid #e5e7eb", flexShrink: 0 }}>
        <li className="nav-item">
          <a
            className={`nav-link ${csp === "aws" ? "active" : ""}`}
            role="button"
            onClick={() => handleCspChange("aws")}
            style={{ fontSize: 14, padding: "6px 16px" }}
          >
            AWS
          </a>
        </li>
        <li className="nav-item">
          <a
            className={`nav-link ${csp === "gcp" ? "active" : ""} text-muted`}
            role="button"
            onClick={() => handleCspChange("gcp")}
            style={{ fontSize: 14, padding: "6px 16px" }}
          >
            GCP
          </a>
        </li>
      </ul>

      {/* AWS content */}
      {csp === "aws" && (
        <div style={{ overflowY: "auto", maxHeight: 420 }}>
          {phase === "checking" && (
            <p className="text-muted mb-0" style={{ fontSize: 14 }}>Checking current setup status…</p>
          )}

          {phase === "configured" && (
            <div className="border border-success rounded p-3" style={{ fontSize: 13 }}>
              <div className="d-flex align-items-center gap-2 mb-3">
                <span className="badge bg-success text-white">Configured</span>
                <span className="text-muted" style={{ fontSize: 12 }}>AWS CUR setup is complete.</span>
              </div>
              <div className="mb-2">
                <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>Credentials</div>
                <span className="text-success" style={{ fontSize: 13 }}>✓ Stored in OpenBao (cost/aws)</span>
              </div>
              <div>
                <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>DB</div>
                <span className="text-success" style={{ fontSize: 13 }}>✓ Registered</span>
              </div>
            </div>
          )}

          {phase === "idle" && (
            <p className="text-muted mb-0" style={{ fontSize: 14 }}>
              Automatically configures IAM user, S3 bucket, and CUR report.
            </p>
          )}

          {(phase === "running" || phase === "done" || phase === "error") && (
            <div>
              {phase === "running" && (
                <p className="text-muted mb-3" style={{ fontSize: 14 }}>
                  Setting up AWS resources. Please wait…
                </p>
              )}

              <div className="mb-3">
                {steps.map((step) => (
                  <div
                    key={step.name}
                    className="d-flex align-items-start gap-2 py-1 border-bottom"
                    style={{ fontSize: 14 }}
                  >
                    <span
                      className={`fw-bold ${STATUS_COLOR[step.status] || "text-secondary"}`}
                      style={{ width: 14, flexShrink: 0, marginTop: 1 }}
                    >
                      {STATUS_ICON[step.status] || "?"}
                    </span>
                    <div className="flex-grow-1">
                      <span>{step.name}</span>
                      {step.status === "SKIP" && step.message && (
                        <span className="text-muted ms-1">({step.message})</span>
                      )}
                      {step.status === "WARN" && step.message && (
                        <div className="text-warning" style={{ fontSize: 12 }}>{step.message}</div>
                      )}
                      {step.status === "FAILED" && step.message && (
                        <div className="text-danger" style={{ fontSize: 12 }}>{step.message}</div>
                      )}
                    </div>
                    <span className={`${STATUS_BADGE[step.status] || "badge bg-light border"} ms-auto`} style={{ fontSize: 12, minWidth: 52, textAlign: "center" }}>
                      {step.status}
                    </span>
                  </div>
                ))}
              </div>

              {phase === "done" && result && (
                <div className="border border-success rounded p-3 mb-0" style={{ fontSize: 13 }}>
                  <div className="mb-2">
                    <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>Bucket</div>
                    <code style={{ wordBreak: "break-all", fontSize: 12 }}>{result.bucketName}</code>
                  </div>
                  <div className="mb-2">
                    <div className="text-muted mb-1" style={{ fontSize: 11, textTransform: "uppercase", letterSpacing: "0.05em" }}>Report</div>
                    <code style={{ fontSize: 12 }}>{result.reportName}</code>
                  </div>
                  <div className="text-muted pt-2 border-top" style={{ fontSize: 12 }}>
                    ⏱ CUR data will be available in S3 within 24 hours.
                  </div>
                </div>
              )}

              {phase === "error" && (
                <div className="alert alert-danger py-2 mb-0" style={{ fontSize: 13 }}>
                  {failedStep ? (
                    <>
                      <div><strong>Failed step:</strong> {failedStep.name}</div>
                      <div><strong>Reason:</strong> {failedStep.message}</div>
                    </>
                  ) : (
                    <div>{errorMsg}</div>
                  )}
                  <div className="mt-1 text-muted" style={{ fontSize: 12 }}>
                    Completed steps will be skipped on retry.
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      )}

      {/* GCP content */}
      {csp === "gcp" && (
        <div className="text-center py-4 text-muted">
          <div style={{ fontSize: 32, marginBottom: 8 }}>☁</div>
          <div className="fw-semibold mb-1">GCP Setup</div>
          <div style={{ fontSize: 13 }}>Coming soon.</div>
        </div>
      )}
    </Modal>
  );
}
