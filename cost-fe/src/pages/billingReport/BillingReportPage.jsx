// src/pages/BillingReportPage.jsx
import Grid from "@/components/layout/Grid";
import BaseInfoCard from "./components/BaseInfoCard";
import InvoiceTable from "./components/InvoiceTable";
import ArchiveStatus from "./components/ArchiveStatus";
import MonthlyOverviewCard from "./components/MonthlyOverviewCard";
import Loading from "@/components/common/loading/Loading";
import { useInvoiceData } from "@/hooks/useInvoiceData";
import { useArchive } from "@/hooks/useArchive";

export default function BillingReportPage() {
  const { baseInfo, summary, invoice, loading } = useInvoiceData();
  // Shared archive state: the invoice card triggers archiving, the card below shows status.
  const { months, status, archiving, purging, runArchive, runPurge, checkGate } = useArchive();

  if (loading) return <Loading fullscreen withLabel label="Loading data..." />;

  return (
    <>
      <Grid cols={2} gap={5} equalHeight>
        <BaseInfoCard
          totalAmount={baseInfo?.reduce((sum, item) => sum + item.cost, 0) || 0}
          providers={baseInfo || []}
        />
        <MonthlyOverviewCard data={summary} />
        <InvoiceTable
          invoice={invoice?.invoice || []}
          colSpan={12}
          months={months}
          archiving={archiving}
          onArchive={runArchive}
        />
        <ArchiveStatus
          colSpan={12}
          status={status}
          purging={purging}
          onPurge={runPurge}
          checkGate={checkGate}
        />
      </Grid>
    </>
  );
}
